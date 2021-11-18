package com.andrewsozonov.urbanride.domain.interactor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.LAT1
import com.andrewsozonov.urbanride.util.TestConstants.LAT2
import com.andrewsozonov.urbanride.util.TestConstants.LAT3
import com.andrewsozonov.urbanride.util.TestConstants.LAT4
import com.andrewsozonov.urbanride.util.TestConstants.LONG1
import com.andrewsozonov.urbanride.util.TestConstants.LONG2
import com.andrewsozonov.urbanride.util.TestConstants.LONG3
import com.andrewsozonov.urbanride.util.TestConstants.LONG4
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.STATUS
import com.andrewsozonov.urbanride.util.TestConstants.TIMER_VALUE
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class RideInteractorTest {


    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val repository: BaseRepository = mockk()
    private val converter: RideConverter = mockk()
    private val rideInteractor = RideInteractor(repository, converter)


//    private val rideDataModel = RideDataModel(DISTANCE_METERS, SPEED_M_S, AVG_SPEED_M_S, testTrackingPoints)
    private lateinit var rideDataModel: RideDataModel
//    private val rideModel = RideModel(DISTANCE_KM, SPEED_KM_H, AVG_SPEED_KM_H, testTrackingPoints)
    private lateinit var rideModel: RideModel
    private val testTimerValue = TIMER_VALUE
    private val testServiceStatus = STATUS

    @Before
    fun setUp() {
        rideDataModel = createRideDataModel()
        rideModel = createRideModel()
        every { repository.getTrackingData() } returns MutableLiveData(rideDataModel)

        every { converter.convertFromRideDataModelToRideModel(
                rideDataModel,
                any()
            )
        } returns rideModel

        every { repository.getTimerValue() } returns MutableLiveData(testTimerValue)
        every { repository.getServiceStatus() } returns MutableLiveData(testServiceStatus)
    }

    /*@Test
    fun testGetTrackingData() {
        val result = rideInteractor.getTrackingData()
        val expectedResult: MutableLiveData<RideModel> = MutableLiveData(rideModel)

        assertThat(result.value).isEqualTo(expectedResult.value)
    }*/

    @Test
    fun testGetTimerValue() {
        val result = rideInteractor.getTimerValue()
        val expectedResult = testTimerValue

        assertThat(result.value).isEqualTo(expectedResult)
    }

    @Test
    fun testGetServiceStatus() {
        val result = rideInteractor.getServiceStatus()
        val expectedResult = testServiceStatus

        assertThat(result.value).isEqualTo(expectedResult)
    }

    private fun createTrackingPoints() : MutableList<MutableList<LatLng>> {
        return mutableListOf(
            mutableListOf(
                LatLng(LAT1, LONG1),
                LatLng(LAT2, LONG2)
            ), mutableListOf(
                LatLng(LAT3, LONG3),
                LatLng(LAT4, LONG4)
            )
        )
    }

    private fun createRideDataModel(): RideDataModel {
        return RideDataModel(DISTANCE_METERS, SPEED_M_S, AVG_SPEED_M_S, createTrackingPoints())
    }

    private fun createRideModel(): RideModel {
        return RideModel(DISTANCE_KM, SPEED_KM_H, AVG_SPEED_KM_H, createTrackingPoints())
    }
}










