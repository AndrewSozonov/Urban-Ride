package com.andrewsozonov.urbanride.data.repository

import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.constants.UnitsContants.MILLIS_IN_SECONDS
import com.andrewsozonov.urbanride.util.constants.UnitsContants.MILLIS_IN_SECONDS_FLOAT
import com.andrewsozonov.urbanride.util.constants.UnitsContants.SCALE
import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode


/**
 * Конвертер репозитория
 *
 * @author Андрей Созонов
 */
class RideRepositoryConverter {

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
        val distance = if (trackingPoints.isNotEmpty() && trackingPoints.last().isNotEmpty()) {
            trackingPoints.last().last().distance
        } else 0f
        val speed = if (trackingPoints.isNotEmpty() && trackingPoints.last().isNotEmpty()) {
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

    private fun calculateAverageSpeed(ridingTime: Long, distance: Float): Float {
        return if (ridingTime > MILLIS_IN_SECONDS) {
            (distance / (ridingTime / MILLIS_IN_SECONDS_FLOAT)).toBigDecimal()
                .setScale(SCALE, RoundingMode.HALF_UP).toFloat()
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