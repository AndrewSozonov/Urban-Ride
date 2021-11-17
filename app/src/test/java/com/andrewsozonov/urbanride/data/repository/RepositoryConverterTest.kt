package com.andrewsozonov.urbanride.data.repository

import android.location.Location
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.TestConstants
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_MS
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import io.mockk.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepositoryConverterTest {

    private val converter = RepositoryConverter()
//


    private val locationPoints: MutableList<MutableList<LocationPoint>> = mutableListOf(
        mutableListOf(
            LocationPoint(TestConstants.LAT1, TestConstants.LONG1, TestConstants.SPEED1_M_S, TestConstants.TIME1_MS, TestConstants.DISTANCE1_METERS),
            LocationPoint(TestConstants.LAT2, TestConstants.LONG2, TestConstants.SPEED2_M_S, TestConstants.TIME2_MS, TestConstants.DISTANCE2_METERS),
        ), mutableListOf(
            LocationPoint(TestConstants.LAT3, TestConstants.LONG3, TestConstants.SPEED3_M_S, TestConstants.TIME3_MS, TestConstants.DISTANCE3_METERS),
            LocationPoint(TestConstants.LAT4, TestConstants.LONG4, TestConstants.SPEED4_M_S, TestConstants.TIME4_MS, TestConstants.DISTANCE4_METERS),
        )
    )

    private val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf(
        mutableListOf(
            LatLng(TestConstants.LAT1, TestConstants.LONG1),
            LatLng(TestConstants.LAT2, TestConstants.LONG2)
        ), mutableListOf(
            LatLng(TestConstants.LAT3, TestConstants.LONG3),
            LatLng(TestConstants.LAT4, TestConstants.LONG4)
        )
    )
    private val rideDataModel =
        RideDataModel(DISTANCE_METERS, TestConstants.SPEED_M_S, TestConstants.AVG_SPEED_M_S, trackingPoints)


    @Test
    fun `test convertDataToRideDataModel`() {

        val result = converter.convertDataToRideDataModel(locationPoints, DURATION_MS)
        val expectedResult = rideDataModel
        Truth.assertThat(result).isEqualTo(expectedResult)

    }
}