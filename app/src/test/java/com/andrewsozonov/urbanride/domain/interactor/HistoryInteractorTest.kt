package com.andrewsozonov.urbanride.domain.interactor

import android.graphics.Bitmap
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.domain.converter.HistoryConverter
import com.andrewsozonov.urbanride.models.data.RideDBModel
import com.andrewsozonov.urbanride.models.presentation.history.HistoryLocationPoint
import com.andrewsozonov.urbanride.models.presentation.history.HistoryModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_KM_H_DB
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM_DB
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_MS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_STRING
import com.andrewsozonov.urbanride.util.TestConstants.FINISH_TIME_MS
import com.andrewsozonov.urbanride.util.TestConstants.FINISH_TIME_STRING
import com.andrewsozonov.urbanride.util.TestConstants.ID
import com.andrewsozonov.urbanride.util.TestConstants.LAT1
import com.andrewsozonov.urbanride.util.TestConstants.LAT2
import com.andrewsozonov.urbanride.util.TestConstants.LAT3
import com.andrewsozonov.urbanride.util.TestConstants.LAT4
import com.andrewsozonov.urbanride.util.TestConstants.LONG1
import com.andrewsozonov.urbanride.util.TestConstants.LONG2
import com.andrewsozonov.urbanride.util.TestConstants.LONG3
import com.andrewsozonov.urbanride.util.TestConstants.LONG4
import com.andrewsozonov.urbanride.util.TestConstants.MAX_SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_M_S
import com.andrewsozonov.urbanride.util.TestConstants.START_DATE_STRING
import com.andrewsozonov.urbanride.util.TestConstants.START_TIME_MS
import com.andrewsozonov.urbanride.util.TestConstants.START_TIME_STRING
import com.andrewsozonov.urbanride.util.TestConstants.TIME1_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME1_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME2_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME2_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME3_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME3_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME4_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME4_MS
import com.google.common.truth.Truth
import io.mockk.*
import org.junit.Before
import org.junit.Test

/**
 * Тест класс для [HistoryInteractor]
 *
 * @author Андрей Созонов
 */
class HistoryInteractorTest {

    private val repository: RideRepository = mockk()
    private val converter: HistoryConverter = mockk()
    private val settings: SettingsRepository = mockk()
    private val interactor = HistoryInteractor(repository, settings, converter)
    private val mapImage: Bitmap = mockk()
    private lateinit var rideDBModel: RideDBModel
    private lateinit var historyModel: HistoryModel
    private val id = ID

    @Before
    fun setUp() {
        rideDBModel = createRideDBModel()
        historyModel = createHistoryModel()
        val listOfRidesFromDB = mutableListOf(rideDBModel)

        every { repository.getAllRides() } returns listOfRidesFromDB
        every { converter.convertFromRideToHistoryModel(rideDBModel, true) } returns historyModel
        every { repository.deleteRide(any()) } just runs
        every { settings.isUnitsMetric() } returns true

    }

    @Test
    fun `test getAllRides`() {
        val listOfRidesConverted = mutableListOf(historyModel)

        val result = interactor.getAllRides()

        Truth.assertThat(result).isEqualTo(listOfRidesConverted)
    }

    @Test
    fun `test deleteRide`() {
        interactor.deleteRide(id)
        verifySequence { repository.deleteRide(id) }
    }

    private fun createRideDBModel(): RideDBModel {

        val trackingPoints = mutableListOf(
            mutableListOf(
                LocationPoint(LAT1, LONG1, SPEED1_M_S, TIME1_MS, DISTANCE1_METERS),
                LocationPoint(LAT2, LONG2, SPEED2_M_S, TIME2_MS, DISTANCE2_METERS),
            ), mutableListOf(
                LocationPoint(LAT3, LONG3, SPEED3_M_S, TIME3_MS, DISTANCE3_METERS),
                LocationPoint(LAT4, LONG4, SPEED4_M_S, TIME4_MS, DISTANCE4_METERS),
            )
        )
        return RideDBModel(
            START_TIME_MS,
            FINISH_TIME_MS,
            DURATION_MS,
            DISTANCE_METERS,
            AVG_SPEED_M_S,
            mapImage,
            trackingPoints
        )
    }

    private fun createHistoryModel(): HistoryModel {
        val historyTrackingPoints = mutableListOf(
            mutableListOf(
                HistoryLocationPoint(LAT1, LONG1, SPEED1_KM_H, TIME1_MIN, DISTANCE1_KM),
                HistoryLocationPoint(LAT2, LONG2, SPEED2_KM_H, TIME2_MIN, DISTANCE2_KM)
            ), mutableListOf(
                HistoryLocationPoint(LAT3, LONG3, SPEED3_KM_H, TIME3_MIN, DISTANCE3_KM),
                HistoryLocationPoint(LAT4, LONG4, SPEED4_KM_H, TIME4_MIN, DISTANCE4_KM)
            )
        )
        return HistoryModel(
            ID,
            START_DATE_STRING,
            START_TIME_STRING,
            FINISH_TIME_STRING,
            DURATION_STRING,
            DISTANCE_KM_DB,
            AVG_SPEED_KM_H_DB,
            MAX_SPEED_KM_H,
            mapImage,
            historyTrackingPoints,
            true
        )
    }
}












