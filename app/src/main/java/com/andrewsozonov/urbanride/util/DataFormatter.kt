package com.andrewsozonov.urbanride.util

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
}