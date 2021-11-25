package com.andrewsozonov.urbanride.domain.converter

import android.graphics.Bitmap
import com.andrewsozonov.urbanride.models.data.RideDBModel
import com.andrewsozonov.urbanride.models.presentation.history.HistoryLocationPoint
import com.andrewsozonov.urbanride.models.presentation.history.HistoryModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.util.DataFormatter
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.DATE_FORMAT_PATTERN
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.FORMAT_PATTERN
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.METERS_IN_KM
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.METERS_IN_KM_DOUBLE
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.MILES_IN_KM
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.MILLIS_IN_SECONDS_DOUBLE
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.SECONDS_IN_HOUR
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.SECONDS_IN_MINUTES
import com.andrewsozonov.urbanride.util.constants.UnitsConstants.TIME_FORMAT_PATTERN
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Конвертирует из модели базы данных данных в модель
 * для отображения на экране History
 *
 * @author Андрей Созонов
 */
class HistoryConverter {

    /**
     * Конвертирует из модели [RideDBModel] в модель [HistoryModel]
     * на основе единиц измерения
     * @param rideDBModel модель данных из БД
     * @param isUnitsMetric единицы измерения из preferences
     */
    fun convertFromRideToHistoryModel(
        rideDBModel: RideDBModel,
        isUnitsMetric: Boolean
    ): HistoryModel {

        val id = rideDBModel.id
        val date = formatDate(rideDBModel.startTime)
        val startTime = formatTime(rideDBModel.startTime)
        val finishTime = formatTime(rideDBModel.finishTime)
        val duration = DataFormatter.formatTime(rideDBModel.duration)
        val distance: Double
        val averageSpeed: Double
        val maxSpeed: Double
        val map: Bitmap = rideDBModel.mapImg
        val trackingPoints: List<List<HistoryLocationPoint>> =
            convertToListHistoryLocationPoint(rideDBModel.trackingPoints, isUnitsMetric)

        if (isUnitsMetric) {
            distance = rideDBModel.distance / METERS_IN_KM_DOUBLE
            averageSpeed = convertSpeedToKmH(rideDBModel.averageSpeed)
            maxSpeed = findMaxSpeed(rideDBModel)
        } else {
            distance = convertKilometersToMiles(rideDBModel.distance / METERS_IN_KM_DOUBLE)
            averageSpeed = convertKilometersToMiles(convertSpeedToKmH(rideDBModel.averageSpeed))
            maxSpeed = convertKilometersToMiles(findMaxSpeed(rideDBModel))
        }

        return HistoryModel(
            id!!,
            date,
            startTime,
            finishTime,
            duration,
            distance,
            averageSpeed,
            maxSpeed,
            map,
            trackingPoints,
            isUnitsMetric
        )
    }

    private fun formatDate(time: Long): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault())
        val startTime = Calendar.getInstance().apply {
            timeInMillis = time
        }
        return dateFormat.format(startTime.time)
    }

    private fun formatTime(time: Long): String {
        val timeFormat = SimpleDateFormat(TIME_FORMAT_PATTERN, Locale.getDefault())
        val startTime = Calendar.getInstance().apply {
            timeInMillis = time
        }
        return timeFormat.format(startTime.time)
    }

    private fun convertToListHistoryLocationPoint(
        trackingPoints: List<List<LocationPoint>>,
        isUnitsMetric: Boolean
    ): List<List<HistoryLocationPoint>> {
        val list: MutableList<MutableList<HistoryLocationPoint>> = mutableListOf()
        trackingPoints.map {
            val historyLine: MutableList<HistoryLocationPoint> = mutableListOf()
            it.map { point ->
                val distance = if (isUnitsMetric) {
                    convertMetersToKilometers(point.distance)
                } else {
                    convertKilometersToMiles(convertMetersToKilometers(point.distance))
                }

                val speed = if (isUnitsMetric) {
                    convertSpeedToKmH(point.speed)
                } else {
                    convertKilometersToMiles(convertSpeedToKmH(point.speed))
                }
                val historyPoint = HistoryLocationPoint(
                    point.latitude,
                    point.longitude,
                    speed,
                    convertMillisecondsToMinutes(point.time),
                    distance
                )

                historyLine.add(historyPoint)
            }
            list.add(historyLine)
        }
        return list
    }

    private fun convertSpeedToKmH(speedMetersPerSecond: Float): Double {
        val speed = speedMetersPerSecond / METERS_IN_KM * SECONDS_IN_HOUR
        return formatValue(speed)
    }

    private fun convertMillisecondsToMinutes(milliseconds: Long): Double {
        val minutes = milliseconds / MILLIS_IN_SECONDS_DOUBLE / SECONDS_IN_MINUTES
        return formatValue(minutes.toFloat())
    }

    private fun convertMetersToKilometers(meters: Float): Double {
        return meters.toInt() / METERS_IN_KM_DOUBLE
    }

    private fun convertKilometersToMiles(kilometers: Double): Double {
        val miles = kilometers * MILES_IN_KM
        return formatValue(miles.toFloat())
    }

    private fun findMaxSpeed(rideDBModel: RideDBModel): Double {
        val maxSpeed = rideDBModel.trackingPoints.maxOf {
            it.maxOf { it.speed }
        }
        return convertSpeedToKmH(maxSpeed)
    }

    private fun formatValue(value: Float): Double {
        val df = DecimalFormat(FORMAT_PATTERN)
        val formattedValue = df.format(value)

        return if (formattedValue.contains(',')) {
            formattedValue.replace(',', '.').toDouble()
        } else {
            formattedValue.toDouble()
        }
    }
}