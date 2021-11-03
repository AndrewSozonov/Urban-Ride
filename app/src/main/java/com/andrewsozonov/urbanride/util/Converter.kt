package com.andrewsozonov.urbanride.util

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
}