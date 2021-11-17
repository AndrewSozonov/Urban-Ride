package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.model.RideDataModel
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
            distance = convertKilometersToMiles(distance)
            speed = convertKilometersToMiles(speed)
            averageSpeed = convertKilometersToMiles(averageSpeed)
        }

        return RideModel(distance, speed, averageSpeed, modelModel.trackingPoints)
    }

    private fun convertSpeedToKmH(speedMetersPerSecond: Float): Float {
        val speed = speedMetersPerSecond / 1000 * 3600
        return formatValue(speed)
    }

    private fun convertKilometersToMiles(kilometers: Float): Float {
        val miles = kilometers * 0.62
        return formatValue(miles.toFloat())
    }

    private fun formatValue(value: Float): Float {
        val df = DecimalFormat("###.##")
        val formattedValue = df.format(value)

        return if (formattedValue.contains(',')) {
            formattedValue.replace(',', '.').toFloat()
        } else {
            formattedValue.toFloat()
        }
    }
}