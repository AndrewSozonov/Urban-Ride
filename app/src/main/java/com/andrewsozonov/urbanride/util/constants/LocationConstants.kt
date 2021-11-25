package com.andrewsozonov.urbanride.util.constants

/**
 * Константы связанные с геолокацией
 *
 * @author Андрей Созонов
 */
object LocationConstants {

    /**
     * action запуска сервиса геолокации
     */
    const val ACTION_START_LOCATION_SERVICE = "START_LOCATION_SERVICE"

    /**
     * action паузы сервиса геолокации
     */
    const val ACTION_PAUSE_LOCATION_SERVICE = "PAUSE_LOCATION_SERVICE"

    /**
     * action стоп сервиса геолокации
     */
    const val ACTION_STOP_LOCATION_SERVICE = "STOP_LOCATION_SERVICE"

    /**
     * id канала нотификации сервиса геолокации
     */
    const val NOTIFICATION_CHANNEL_ID = "LOCATION_CHANNEL"

    /**
     * имя канала нотификации сервиса геолокации
     */
    const val NOTIFICATION_CHANNEL_NAME = "LOCATION"

    /**
     * id нотификации сервиса геолокации
     */
    const val NOTIFICATION_ID = 1

    /**
     * action навигации к фрагменту RideFragment
     */
    const val ACTION_SHOW_RIDING_FRAGMENT = "ACTION_SHOW_RIDING_FRAGMENT"

    /**
     * Интервал обновления геолокации
     */
    const val LOCATION_UPDATE_INTERVAL = 2000L

    /**
     * Интервал обновления таймера
     */
    const val TIMER_DELAY = 1000L
}