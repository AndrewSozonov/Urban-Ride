package com.andrewsozonov.urbanride.domain.converter

import android.graphics.Bitmap
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.history.model.HistoryLocationPoint
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_KM_H_DB
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_ML_H_DB
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_ML
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_ML
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_ML
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_ML
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM_DB
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_ML_DB
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
import com.andrewsozonov.urbanride.util.TestConstants.MAX_SPEED_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_ML_H
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import org.junit.Before
import org.junit.Test
import java.util.*

class HistoryConverterTest {

    private val converter = HistoryConverter()
    private val mapImage: Bitmap = mockk()

    private lateinit var rideDBModel: RideDBModel
    private lateinit var historyModelKm: HistoryModel
    private lateinit var historyModelMiles: HistoryModel

    @Before
    fun setUp() {
        rideDBModel = createRideDBModel()
        historyModelKm = createHistoryModelKm()
        historyModelMiles = createHistoryModelMiles()
    }

    @Test
    fun `test convertFromRideToHistoryModel units kilometers`() {
        rideDBModel.id = ID
        val result = converter.convertFromRideToHistoryModel(rideDBModel, true)
        val expectedResult = historyModelKm

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `test convertFromRideToHistoryModel units miles`() {
        rideDBModel.id = ID
        val result = converter.convertFromRideToHistoryModel(rideDBModel, false)
        val expectedResult = historyModelMiles

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    private fun createTrackingPoints(): MutableList<MutableList<LocationPoint>> {
        return mutableListOf(
            mutableListOf(
                LocationPoint(LAT1, LONG1, SPEED1_M_S, TIME1_MS, DISTANCE1_METERS),
                LocationPoint(LAT2, LONG2, SPEED2_M_S, TIME2_MS, DISTANCE2_METERS),
            ), mutableListOf(
                LocationPoint(LAT3, LONG3, SPEED3_M_S, TIME3_MS, DISTANCE3_METERS),
                LocationPoint(LAT4, LONG4, SPEED4_M_S, TIME4_MS, DISTANCE4_METERS),
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

    private fun createHistoryTrackingPointsKm(): MutableList<MutableList<HistoryLocationPoint>> {
        return mutableListOf(
            mutableListOf(
                HistoryLocationPoint(LAT1, LONG1, SPEED1_KM_H, TIME1_MIN, DISTANCE1_KM),
                HistoryLocationPoint(LAT2, LONG2, SPEED2_KM_H, TIME2_MIN, DISTANCE2_KM)
            ), mutableListOf(
                HistoryLocationPoint(LAT3, LONG3, SPEED3_KM_H, TIME3_MIN, DISTANCE3_KM),
                HistoryLocationPoint(LAT4, LONG4, SPEED4_KM_H, TIME4_MIN, DISTANCE4_KM)
            )
        )
    }

    private fun createHistoryModelKm(): HistoryModel {
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
            createHistoryTrackingPointsKm(),
            true
        )
    }

    private fun createHistoryTrackingPointsMiles(): MutableList<MutableList<HistoryLocationPoint>> {
        return mutableListOf(
            mutableListOf(
                HistoryLocationPoint(LAT1, LONG1, SPEED1_ML_H, TIME1_MIN, DISTANCE1_ML),
                HistoryLocationPoint(LAT2, LONG2, SPEED2_ML_H, TIME2_MIN, DISTANCE2_ML)
            ), mutableListOf(
                HistoryLocationPoint(LAT3, LONG3, SPEED3_ML_H, TIME3_MIN, DISTANCE3_ML),
                HistoryLocationPoint(LAT4, LONG4, SPEED4_ML_H, TIME4_MIN, DISTANCE4_ML)
            )
        )
    }

    private fun createHistoryModelMiles(): HistoryModel {
        return HistoryModel(
            ID,
            START_DATE_STRING,
            START_TIME_STRING,
            FINISH_TIME_STRING,
            DURATION_STRING,
            DISTANCE_ML_DB,
            AVG_SPEED_ML_H_DB,
            MAX_SPEED_ML_H,
            mapImage,
            createHistoryTrackingPointsMiles(),
            false
        )
    }
}