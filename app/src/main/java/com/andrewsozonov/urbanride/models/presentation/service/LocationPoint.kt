package com.andrewsozonov.urbanride.models.presentation.service

/**
 * Модель данных точки геолокации получаемой из сервиса
 *
 * @param latitude широта
 * @param longitude долгота
 * @param speed скорость в данной точке в м/c
 * @param time время поездки в данной точке в миллисекундах
 * @param distance пройденное расстояние в данной точке в метрах
 *
 * @author Андрей Созонов
 */
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val time: Long,
    var distance: Float
)
