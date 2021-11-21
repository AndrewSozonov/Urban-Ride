package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import java.text.DecimalFormat

/**
 * Конвертирует из модели данных data в модель для отображения на главном экране
 *
 * @author Андрей Созонов
 */
class RideConverter {

    /**
     * Конвертирует из модели [RideDataModel] в модель [RideModel]
     * @param  dataModel модель данных data слоя
     * @return модель данных [RideModel]
     */
    fun convertFromRideDataModelToRideModel(
        dataModel: RideDataModel,
        isUnitsMetric: Boolean
    ): RideModel {

        var distance: Float = dataModel.distance / 1000
        var speed: Float = convertSpeedToKmH(dataModel.speed)
        var averageSpeed: Float = convertSpeedToKmH(dataModel.averageSpeed)

        if (!isUnitsMetric) {
            distance = convertKilometersToMiles(distance)
            speed = convertKilometersToMiles(speed)
            averageSpeed = convertKilometersToMiles(averageSpeed)
        }

        return RideModel(distance, speed, averageSpeed, dataModel.trackingPoints, isUnitsMetric)
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