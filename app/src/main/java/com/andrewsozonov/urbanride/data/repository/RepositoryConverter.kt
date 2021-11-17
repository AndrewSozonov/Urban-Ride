package com.andrewsozonov.urbanride.data.repository

import android.location.Location
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode


/**
 * Конвертер репозитория
 *
 * @author Андрей Созонов
 */
class RepositoryConverter {

    /**
     * Конвертирует данные полученные из сервиса геолокации в модель
     *
     * @param trackingPoints список координат геолокации из сервиса
     * @param ridingTime время поездки на текущий момент в миллисекундах
     * @return возвращает модель данных [RideDataModel]
     */
    fun convertDataToRideDataModel(
        trackingPoints: List<List<LocationPoint>>,
        ridingTime: Long
    ): RideDataModel {
        val distance = calculateDistance(trackingPoints)
        val speed = if (trackingPoints.last().isNotEmpty()) {
            trackingPoints.last().last().speed
        } else 0f
        val averageSpeed = calculateAverageSpeed(ridingTime, distance)

        return RideDataModel(
            distance, // метры
            speed, // метры в сек
            averageSpeed,  // метры в сек
            convertLocationPointToLatLng(trackingPoints)
        )
    }

    fun calculateDistance(trackingPoints: List<List<LocationPoint>>): Float {
        var distance = 0.0
        for (path in trackingPoints) {
            for (i in 0..path.size - 2) {
                val point1 = path[i]
                val point2 = path[i + 1]
                val result = FloatArray(1)
                Location.distanceBetween(
                    point1.latitude,
                    point1.longitude,
                    point2.latitude,
                    point2.longitude,
                    result
                )
                distance += result[0]
            }
        }
        return distance.toInt().toFloat()
    }

    private fun calculateAverageSpeed(ridingTime: Long, distance: Float): Float {
        return if (ridingTime > 1000) {
            (distance / (ridingTime / 1000f)).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP).toFloat()
        } else 0.0f
    }

    private fun convertLocationPointToLatLng(points: List<List<LocationPoint>>): List<List<LatLng>> {
        val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf()
        for (list in points) {
            val listLatLng: MutableList<LatLng> = mutableListOf()
            for (point in list) {
                val latLng = LatLng(point.latitude, point.longitude)
                listLatLng.add(latLng)
            }
            trackingPoints.add(listLatLng)
        }
        return trackingPoints
    }
}