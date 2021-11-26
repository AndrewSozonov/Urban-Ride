package com.andrewsozonov.urbanride.data.repository

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andrewsozonov.urbanride.data.database.RideDao
import com.andrewsozonov.urbanride.models.data.RideDBModel
import com.andrewsozonov.urbanride.models.data.RideDataModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_MS
import com.andrewsozonov.urbanride.util.TestConstants.FINISH_TIME_MS
import com.andrewsozonov.urbanride.util.TestConstants.ID
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.START_TIME_MS
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

/**
 * Тест класс для [RideRepositoryImpl]
 *
 * @author Андрей Созонов
 */
class RideRepositoryImplTest {

    private val rideDao: RideDao = mockk()
    private val converterRide: RideRepositoryConverter = mockk()
    private val mapImage: Bitmap = mockk()
    private lateinit var rideDBModel: RideDBModel
    private val repository: RideRepositoryImpl = RideRepositoryImpl(rideDao, converterRide)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        rideDBModel = createRideDBModel()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance().timeInMillis } returns FINISH_TIME_MS
        every { rideDao.addRide(createRideDBModel()) } just runs
    }

    @Test
    fun `test addRide`() {
        repository.trackingPoints = createTrackingPoints()
        repository.ridingTimeInMillis = DURATION_MS
        repository.distanceInMeters = DISTANCE_METERS
        repository.speedInMpS = SPEED_M_S
        repository.averageSpeedInMpS = AVG_SPEED_M_S

        repository.addRide(mapImage)
        verifySequence { rideDao.addRide(createRideDBModel()) }
    }

    @Test
    fun `test updateLocation`() {
        every {
            converterRide.convertDataToRideDataModel(
                any(),
                any()
            )
        } returns createRideDataModel()
        repository.updateLocation(createTrackingPoints())
        val trackingPoints = repository.trackingPoints

        Truth.assertThat(trackingPoints).isEqualTo(createTrackingPoints())
    }

    @Test
    fun `test deleteRide`() {
        every { rideDao.deleteRide(ID) } just runs
        repository.deleteRide(ID)

        verifySequence { rideDao.deleteRide(ID) }
    }

    @Test
    fun `test getAllRides`() {
        every { rideDao.getAllRides() } returns mutableListOf(
            createRideDBModel(),
            createRideDBModel()
        )

        val result = repository.getAllRides()
        Truth.assertThat(result).isEqualTo(mutableListOf(createRideDBModel(), createRideDBModel()))
    }

    @Test
    fun `test getRideById`() {
        every { rideDao.getRideByID(ID) } returns createRideDBModel()

        val result = repository.getRideById(ID)
        Truth.assertThat(result).isEqualTo(createRideDBModel())
    }

    @Test
    fun `test getTimerValue`() {
        val result = repository.getTimerValue()
        Truth.assertThat(result).isEqualTo(repository.timerLiveData)
    }

    @Test
    fun `test getServiceStatus`() {
        val result = repository.getServiceStatus()
        Truth.assertThat(result).isEqualTo(repository.serviceStatusLiveData)
    }

    @Test
    fun `test getTrackingData`() {
        val result = repository.getTrackingData()
        Truth.assertThat(result).isEqualTo(repository.data)
    }


    private fun createTrackingPoints(): MutableList<MutableList<LocationPoint>> {

        return mutableListOf(
            mutableListOf(
                LocationPoint(
                    TestConstants.LAT1,
                    TestConstants.LONG1,
                    TestConstants.SPEED1_M_S,
                    TestConstants.TIME1_MS,
                    TestConstants.DISTANCE1_METERS
                ),
                LocationPoint(
                    TestConstants.LAT2,
                    TestConstants.LONG2,
                    TestConstants.SPEED2_M_S,
                    TestConstants.TIME2_MS,
                    TestConstants.DISTANCE2_METERS
                ),
            ), mutableListOf(
                LocationPoint(
                    TestConstants.LAT3,
                    TestConstants.LONG3,
                    TestConstants.SPEED3_M_S,
                    TestConstants.TIME3_MS,
                    TestConstants.DISTANCE3_METERS
                ),
                LocationPoint(
                    TestConstants.LAT4,
                    TestConstants.LONG4,
                    TestConstants.SPEED4_M_S,
                    TestConstants.TIME4_MS,
                    TestConstants.DISTANCE4_METERS
                ),
            )
        )
    }

    private fun createRideDBModel(): RideDBModel {

        return RideDBModel(
            START_TIME_MS,
            FINISH_TIME_MS,
            DURATION_MS,
            DISTANCE_METERS,
            AVG_SPEED_M_S,
            mapImage,
            createTrackingPoints()
        )
    }

    private fun createRideDataModel(): RideDataModel {

        val points = mutableListOf(
            mutableListOf(
                LatLng(TestConstants.LAT1, TestConstants.LONG1),
                LatLng(TestConstants.LAT2, TestConstants.LONG2)
            ), mutableListOf(
                LatLng(TestConstants.LAT3, TestConstants.LONG3),
                LatLng(TestConstants.LAT4, TestConstants.LONG4)
            )
        )
        return RideDataModel(DISTANCE_METERS, SPEED_M_S, AVG_SPEED_M_S, points)
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }
}