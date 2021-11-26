package com.andrewsozonov.urbanride.util.constants

/**
 * Константы связанные с картой
 *
 * @author Андрей Созонов
 */
object MapConstants {

    /**
     * Толщина линии рисования маршрута
     */
    const val POLYLINE_WIDTH = 12f

    /**
     * Значение zoom карты во время движения
     */
    const val CAMERA_ZOOM_VALUE = 16f

    /**
     * Значение scalе карты после отрисовки всего маршрута после остановки
     */
    const val CAMERA_ZOOM_SCALING_AFTER_STOP = 0.1f

    /**
     * Размер стрелки маршрута на карте
     */
    const val CUSTOM_CAP_WIDTH = 70f
}