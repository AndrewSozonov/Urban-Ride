package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_ML
import com.andrewsozonov.urbanride.util.TestConstants.LAT1
import com.andrewsozonov.urbanride.util.TestConstants.LAT2
import com.andrewsozonov.urbanride.util.TestConstants.LAT3
import com.andrewsozonov.urbanride.util.TestConstants.LAT4
import com.andrewsozonov.urbanride.util.TestConstants.LONG1
import com.andrewsozonov.urbanride.util.TestConstants.LONG2
import com.andrewsozonov.urbanride.util.TestConstants.LONG3
import com.andrewsozonov.urbanride.util.TestConstants.LONG4
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_ML_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED_M_S
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth
import org.junit.Test

class RideConverterTest {

    private val converter: RideConverter = RideConverter()
    private val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf(
        mutableListOf(
            LatLng(LAT1, LONG1),
            LatLng(LAT2, LONG2)
        ), mutableListOf(
            LatLng(LAT3, LONG3),
            LatLng(LAT4, LONG4)
        )
    )
    private val rideDataModel =
        RideDataModel(DISTANCE_METERS, SPEED_M_S, AVG_SPEED_M_S, trackingPoints)
    private val rideModelMetric =
        RideModel(DISTANCE_KM, SPEED_KM_H, AVG_SPEED_KM_H, trackingPoints)
    private val rideModelMiles =
        RideModel(DISTANCE_ML, SPEED_ML_H, AVG_SPEED_ML_H, trackingPoints)

    @Test
    fun `test ConvertFromRideDataModelToRideModel unitsMetric`() {
        val result = converter.convertFromRideDataModelToRideModel(rideDataModel, true)
        val expectedResult = rideModelMetric

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `test ConvertFromRideDataModelToRideModel unitsMiles`() {
        val result = converter.convertFromRideDataModelToRideModel(rideDataModel, false)
        val expectedResult = rideModelMiles

        Truth.assertThat(result).isEqualTo(expectedResult)
    }
}