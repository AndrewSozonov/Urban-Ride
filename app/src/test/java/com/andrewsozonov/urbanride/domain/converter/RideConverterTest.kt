package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.models.data.RideDataModel
import com.andrewsozonov.urbanride.models.presentation.ride.RideModel
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
    private lateinit var rideDataModel: RideDataModel
    private lateinit var rideModelMetric: RideModel
    private lateinit var rideModelMiles: RideModel

    @Test
    fun `test ConvertFromRideDataModelToRideModel unitsMetric`() {
        rideDataModel = createRideDataModel()
        rideModelMetric = createRideModelMetric()
        val result = converter.convertFromRideDataModelToRideModel(rideDataModel, true)
        val expectedResult = rideModelMetric

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `test ConvertFromRideDataModelToRideModel unitsMiles`() {
        rideDataModel = createRideDataModel()
        rideModelMiles = createRideModelMiles()
        val result = converter.convertFromRideDataModelToRideModel(rideDataModel, false)
        val expectedResult = rideModelMiles

        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    private fun createTrackingPoints(): MutableList<MutableList<LatLng>> {
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

    private fun createRideModelMetric(): RideModel {
        return RideModel(DISTANCE_KM, SPEED_KM_H, AVG_SPEED_KM_H, createTrackingPoints(), true)
    }

    private fun createRideModelMiles(): RideModel {
        return RideModel(DISTANCE_ML, SPEED_ML_H, AVG_SPEED_ML_H, createTrackingPoints(), false)
    }
}