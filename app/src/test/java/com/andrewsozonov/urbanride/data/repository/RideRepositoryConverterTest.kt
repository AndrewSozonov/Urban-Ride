package com.andrewsozonov.urbanride.data.repository

import com.andrewsozonov.urbanride.models.data.RideDataModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_MS
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
import com.andrewsozonov.urbanride.util.TestConstants.TIME1_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME2_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME3_MS
import com.andrewsozonov.urbanride.util.TestConstants.TIME4_MS
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import org.junit.Test

class RideRepositoryConverterTest {

    private val converter = RideRepositoryConverter()
    private lateinit var locationPoints: MutableList<MutableList<LocationPoint>>
    private lateinit var rideDataModel: RideDataModel


    @Test
    fun `test convertDataToRideDataModel`() {
        locationPoints = createLocationPoints()
        val result = converter.convertDataToRideDataModel(locationPoints, DURATION_MS)

        rideDataModel = createRideDataModel()
        Truth.assertThat(result).isEqualTo(rideDataModel)

    }

    private fun createLocationPoints(): MutableList<MutableList<LocationPoint>> {
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

    private fun createRideDataModel(): RideDataModel {
        val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf(
            mutableListOf(
                LatLng(LAT1, LONG1),
                LatLng(LAT2, LONG2)
            ), mutableListOf(
                LatLng(LAT3, LONG3),
                LatLng(LAT4, LONG4)
            )
        )
        return RideDataModel(
            DISTANCE_METERS,
            TestConstants.SPEED_M_S,
            TestConstants.AVG_SPEED_M_S,
            trackingPoints
        )
    }
}