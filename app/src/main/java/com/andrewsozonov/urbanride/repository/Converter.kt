package com.andrewsozonov.urbanride.repository

import android.location.Location
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode
import java.text.DecimalFormat

class Converter {



    fun convertDataToRideDataModel(trackingPoints: List<List<LocationPoint>>, ridingTime: Long): RideDataModel {
        val distance = calculateDistance(trackingPoints)
        val speed = if (trackingPoints.last().isNotEmpty()) {
            convertSpeedToKmH(trackingPoints.last().last().speed)
        } else 0f
        val averageSpeed = calculateAverageSpeed(ridingTime, distance)




        return RideDataModel(
            distance,
            speed,
            averageSpeed,
            convertLocationPointToLatLng(trackingPoints)
        )



    }

    /**
     * Вычисляет расстояние из списка с координатами
     *
     * @param trackingPoints список из списков с координатами
     * @return возвращает расстояние между всеми координатами в км
     */
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
        return distance.toInt().toFloat() / 1000
    }

    /**
     * Вычисляет скорость за все пройденное на данный момент расстояние
     *
     * @param ridingTime время поездки
     * @param distance расстояние поездки
     * @return возвращает скорость в км/ч
     */
    fun calculateAverageSpeed(ridingTime: Long, distance: Float): Float {

        return if (ridingTime > 1000) {
            (distance / (ridingTime / 1000f / 60 / 60)).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP).toFloat()
        } else 0.0f
    }

    fun convertFromRideToRideDataModel(ride: Ride): RideDataModel {
        return RideDataModel(ride.distance, ride.trackingPoints.last().last().speed, ride.averageSpeed, convertLocationPointToLatLng(ride.trackingPoints))
    }

    fun convertLocationPointToLatLng(points: List<List<LocationPoint>>): List<List<LatLng>> {
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

    fun convertSpeedOfList(trackingPoints: List<List<LocationPoint>>): List<List<LocationPoint>> {
        for (line in trackingPoints) {
            for (point in line) {
                point.speed = convertSpeedToKmH(point.speed)
            }
        }
        return trackingPoints
    }

    /*fun convertSpeedToKmH(trackingPoints: List<List<LocationPoint>>) : Float{
        val speed = if (trackingPoints.last().isNotEmpty()) trackingPoints.last()
            .last().speed / 1000 * 3600 else 0f
        val df = DecimalFormat("###.##")
        return df.format(speed).toDouble().toFloat()
    }*/

    fun convertSpeedToKmH(speedMetersPerSecond: Float) : Float{
        val speed = speedMetersPerSecond / 1000 * 3600
        val df = DecimalFormat("###.##")
        return df.format(speed).toDouble().toFloat()
    }


}