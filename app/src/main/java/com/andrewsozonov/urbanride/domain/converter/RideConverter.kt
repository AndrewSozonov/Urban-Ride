package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.constants.UnitsContants.FORMAT_PATTERN
import com.andrewsozonov.urbanride.util.constants.UnitsContants.METERS_IN_KM
import com.andrewsozonov.urbanride.util.constants.UnitsContants.MILES_IN_KM
import com.andrewsozonov.urbanride.util.constants.UnitsContants.SECONDS_IN_HOUR
import java.text.DecimalFormat

/**
 * Конвертирует из модели данных data слоя в модель для отображения на главном экране
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

        val distance: Float
        val speed: Float
        val averageSpeed: Float

        if (isUnitsMetric) {
            distance = dataModel.distance / METERS_IN_KM
            speed = convertSpeedToKmH(dataModel.speed)
            averageSpeed = convertSpeedToKmH(dataModel.averageSpeed)
        } else {
            distance = convertKilometersToMiles(dataModel.distance / METERS_IN_KM)
            speed = convertKilometersToMiles(convertSpeedToKmH(dataModel.speed))
            averageSpeed = convertKilometersToMiles(convertSpeedToKmH(dataModel.averageSpeed))
        }

        return RideModel(distance, speed, averageSpeed, dataModel.trackingPoints, isUnitsMetric)
    }

    private fun convertSpeedToKmH(speedMetersPerSecond: Float): Float {
        val speed = speedMetersPerSecond / METERS_IN_KM * SECONDS_IN_HOUR
        return formatValue(speed)
    }

    private fun convertKilometersToMiles(kilometers: Float): Float {
        val miles = kilometers * MILES_IN_KM
        return formatValue(miles.toFloat())
    }

    private fun formatValue(value: Float): Float {
        val df = DecimalFormat(FORMAT_PATTERN)
        val formattedValue = df.format(value)

        return if (formattedValue.contains(',')) {
            formattedValue.replace(',', '.').toFloat()
        } else {
            formattedValue.toFloat()
        }
    }
}