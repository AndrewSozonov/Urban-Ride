package com.andrewsozonov.urbanride.util

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * Форматирует данные для отображения на экране
 *
 * @author Андрей Созонов
 */
object DataFormatter {

    /**
     * Ковертирует миллисекунды в строку
     *
     * @param time время в миллисекундах
     * @return возвращает строку в формате HH:MM:SS
     */
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

    fun convertMillisecondsToMinutes(milliseconds: Long): Double {
        val df = DecimalFormat("###.##")
        return df.format(milliseconds / 1000.0 / 60.0).toDouble()
    }
}