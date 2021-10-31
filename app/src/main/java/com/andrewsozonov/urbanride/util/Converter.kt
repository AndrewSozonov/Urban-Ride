package com.andrewsozonov.urbanride.util

import java.text.DecimalFormat

object Converter {

    fun convertKilometersToMiles(kilometers: Float): Double {
        val miles = kilometers * 0.62

        val df = DecimalFormat("###.##")
        return df.format(miles).toDouble()
    }
}