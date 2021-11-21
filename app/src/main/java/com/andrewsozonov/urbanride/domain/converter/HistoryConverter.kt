package com.andrewsozonov.urbanride.domain.converter

import android.graphics.Bitmap
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.history.model.HistoryLocationPoint
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.DataFormatter
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
    fun convertFromRideToHistoryModel(rideDBModel: RideDBModel, isUnitsMetric: Boolean): HistoryModel {

        val id = rideDBModel.id
        val date = formatDate(rideDBModel.startTime)
        val startTime = formatTime(rideDBModel.startTime)
        val finishTime = formatTime(rideDBModel.finishTime)
        val duration = DataFormatter.formatTime(rideDBModel.duration)
        var distance = rideDBModel.distance / 1000.0
        var averageSpeed = convertSpeedToKmH(rideDBModel.averageSpeed)
        var maxSpeed = findMaxSpeed(rideDBModel)
        val map: Bitmap = rideDBModel.mapImg
        val trackingPoints: List<List<HistoryLocationPoint>> =
            convertToListHistoryLocationPoint(rideDBModel.trackingPoints, isUnitsMetric)

        if (!isUnitsMetric) {
            distance = convertKilometersToMiles(distance)
            averageSpeed = convertKilometersToMiles(averageSpeed)
            maxSpeed = convertKilometersToMiles(maxSpeed)
        }

        return HistoryModel(id!!, date, startTime, finishTime, duration, distance, averageSpeed, maxSpeed, map, trackingPoints, isUnitsMetric)
    }

    private fun formatDate(time: Long): String {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val startTime = Calendar.getInstance().apply {
            timeInMillis = time
        }
        return dateFormat.format(startTime.time)
    }

    private fun formatTime(time: Long): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
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
            it.map {point ->
                val historyPoint = HistoryLocationPoint(
                    point.latitude,
                    point.longitude,
                    convertSpeedToKmH(point.speed),
                    convertMillisecondsToMinutes(point.time),
                    convertMetersToKilometers(point.distance)
                )
                if (!isUnitsMetric) {
                    historyPoint.distance = convertKilometersToMiles(historyPoint.distance)
                    historyPoint.speed = convertKilometersToMiles(historyPoint.speed)
                }
                historyLine.add(historyPoint)
            }
            list.add(historyLine)
        }
        return list
    }

    private fun convertSpeedToKmH(speedMetersPerSecond: Float): Double {
        val speed = speedMetersPerSecond / 1000 * 3600
        return formatValue(speed)
    }

    private fun convertMillisecondsToMinutes(milliseconds: Long): Double {
        val minutes = milliseconds / 1000.0 / 60.0
        return formatValue(minutes.toFloat())
    }

    private fun convertMetersToKilometers(meters: Float): Double {
        return meters.toInt() / 1000.0
    }

    private fun convertKilometersToMiles(kilometers: Double): Double {
        val miles = kilometers * 0.62
        return formatValue(miles.toFloat())
    }

    private fun findMaxSpeed(rideDBModel: RideDBModel): Double {
        val maxSpeed = rideDBModel.trackingPoints.maxOf {
            it.maxOf { it.speed }
        }
        return convertSpeedToKmH(maxSpeed)
    }

    private fun formatValue(value: Float): Double {
        val df = DecimalFormat("###.##")
        val formattedValue = df.format(value)

        return if (formattedValue.contains(',')) {
            formattedValue.replace(',', '.').toDouble()
        } else {
            formattedValue.toDouble()
        }
    }
}