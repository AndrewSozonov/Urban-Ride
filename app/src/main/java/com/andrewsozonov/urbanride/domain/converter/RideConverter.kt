package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.RideDataModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import java.text.DecimalFormat

class RideConverter {

    fun convertFromRideDataModelToRideModel(
        modelModel: RideDataModel,
        isUnitsMetric: Boolean
    ): RideModel {

        var distance: Float = modelModel.distance / 1000
        var speed: Float = convertSpeedToKmH(modelModel.speed)
        var averageSpeed: Float = convertSpeedToKmH(modelModel.averageSpeed)

        if (!isUnitsMetric) {
            distance = convertKilometersToMiles(distance).toFloat()
            speed = convertKilometersToMiles(speed).toFloat()
            averageSpeed = convertKilometersToMiles(averageSpeed).toFloat()
        }

        return RideModel(distance, speed, averageSpeed, modelModel.trackingPoints)
    }


    private fun convertSpeedToKmH(speedMetersPerSecond: Float): Float {
        val speed = speedMetersPerSecond / 1000 * 3600
        val df = DecimalFormat("###.##")
        return df.format(speed).toDouble().toFloat()
    }

    private fun convertKilometersToMiles(kilometers: Float): Double {
        val miles = kilometers * 0.62

        val df = DecimalFormat("###.##")
        return df.format(miles).toDouble()
    }
}