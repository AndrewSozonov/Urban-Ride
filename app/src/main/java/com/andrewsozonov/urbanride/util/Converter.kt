package com.andrewsozonov.urbanride.util

import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

/**
 * Конвертирует из одних единици измерения в другие
 *
 * @author Андрей Созонов
 */
object Converter {


    /**
     * Конвертирует из километров в мили
     *
     * @return возращает значение в милях
     */
    fun convertKilometersToMiles(kilometers: Float): Double {
        val miles = kilometers * 0.62

        val df = DecimalFormat("###.##")
        return df.format(miles).toDouble()
    }

    fun convertLocationPointToLatLng(point: LocationPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    fun convertListLocationPointToListLatLng(points: List<List<LocationPoint>>): List<List<LatLng>> {
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