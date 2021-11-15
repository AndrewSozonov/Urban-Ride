package com.andrewsozonov.urbanride.presentation.history.model

/**
 *  Модель данных точки геолокации для экрана History
 *
 *  @param latitude широта
 *  @param longitude долгота
 *  @param speed скорость в данной точке в км/ч или милях/ч
 *  @param time время поездки в данной точке в минутах
 *  @param distance пройденное расстояние в данной точке в км или милях
 */
data class HistoryLocationPoint(var latitude: Double, var longitude: Double, var speed: Double, var time: Double, var distance: Double)
