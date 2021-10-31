package com.andrewsozonov.urbanride.util

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

object DataFormatter {

    fun formatTime(time: Long): String {
        var millisecs = time
        val hours = TimeUnit.MILLISECONDS.toHours(millisecs)
        millisecs -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecs)
        millisecs -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecs)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    fun calculateDistance(trackingPoints: List<List<LatLng>>): Float {
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

    fun calculateSpeed(ridingTime: Long, trackingPoints: List<List<LatLng>>): Float {

        if (trackingPoints.isNotEmpty() && trackingPoints.last().size > 1 && ridingTime > 1000) {

            val currentLatLng = trackingPoints.last().last()
            val lastLatLng = trackingPoints.last()[trackingPoints.last().size - 2]

            val result = FloatArray(1)
            Location.distanceBetween(
                currentLatLng.latitude,
                currentLatLng.longitude,
                lastLatLng.latitude,
                lastLatLng.longitude,
                result
            )

            return ((result[0] / 1000f) / (ridingTime / 1000f / 60 / 60)).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP).toFloat()

        } else
            return 0.0f
    }

    fun calculateAverageSpeed(ridingTime: Long, distance: Float): Float {

        return if (ridingTime > 1000) {
            (distance / (ridingTime / 1000f / 60 / 60)).toBigDecimal()
                .setScale(2, RoundingMode.HALF_UP).toFloat()
        } else 0.0f
    }
}