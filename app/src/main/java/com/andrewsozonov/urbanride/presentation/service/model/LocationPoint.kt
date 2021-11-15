package com.andrewsozonov.urbanride.presentation.service.model


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
data class LocationPoint(var latitude: Double, var longitude: Double, var speed: Float, var time: Long, var distance: Float)
