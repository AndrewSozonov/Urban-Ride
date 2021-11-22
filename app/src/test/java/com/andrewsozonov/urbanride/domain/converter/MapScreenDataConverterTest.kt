package com.andrewsozonov.urbanride.domain.converter

import android.graphics.Bitmap
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_MS
import com.andrewsozonov.urbanride.util.TestConstants.FINISH_TIME_MS
import com.andrewsozonov.urbanride.util.TestConstants.LAT1
import com.andrewsozonov.urbanride.util.TestConstants.LAT2
import com.andrewsozonov.urbanride.util.TestConstants.LAT3
import com.andrewsozonov.urbanride.util.TestConstants.LAT4
import com.andrewsozonov.urbanride.util.TestConstants.LONG1
import com.andrewsozonov.urbanride.util.TestConstants.LONG2
import com.andrewsozonov.urbanride.util.TestConstants.LONG3
import com.andrewsozonov.urbanride.util.TestConstants.LONG4
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_M_S
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_M_S
import com.andrewsozonov.urbanride.util.TestConstants.START_TIME_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME1_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME2_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME3_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME4_MS
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class MapScreenDataConverterTest {

    private val converter = MapScreenDataConverter()
    private val mapImage: Bitmap = mockk()
    private lateinit var rideDBModel: RideDBModel
    private lateinit var rideModel: RideModel

    @Before
    fun setUp() {
        rideDBModel = createRideDBModel()
        rideModel = createRideModel()
    }

    @Test
    fun `test convertFromRideDBModelToRideModel`() {
        val result = converter.convertFromRideDBModelToRideModel(rideDBModel)
        val expectedResult = rideModel

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    private fun createRideDBModel(): RideDBModel {

        val locationPoints: MutableList<MutableList<LocationPoint>> = mutableListOf(
            mutableListOf(
                LocationPoint(LAT1, LONG1, SPEED1_M_S, TIME1_MS, DISTANCE1_METERS),
                LocationPoint(LAT2, LONG2, SPEED2_M_S, TIME2_MS, DISTANCE2_METERS),
            ), mutableListOf(
                LocationPoint(LAT3, LONG3, SPEED3_M_S, TIME3_MS, DISTANCE3_METERS),
                LocationPoint(LAT4, LONG4, SPEED4_M_S, TIME4_MS, DISTANCE4_METERS),
            )
        )
        return  RideDBModel(
            START_TIME_MS,
            FINISH_TIME_MS,
            DURATION_MS,
            DISTANCE_METERS,
            AVG_SPEED_M_S,
            mapImage,
            locationPoints
        )
    }

    private fun createRideModel(): RideModel {

        val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf(
            mutableListOf(
                LatLng(LAT1, LONG1), LatLng(LAT2, LONG2),
            ), mutableListOf(
                LatLng(LAT3, LONG3), LatLng(LAT4, LONG4),
            )
        )
        return RideModel(DISTANCE_KM, SPEED4_M_S, AVG_SPEED_M_S, trackingPoints, true)
    }
}