package com.andrewsozonov.urbanride.model


/**
 * Модель данных для отображения в главном фрагменте
 *
 * @param distance пройденное расстояние в км
 * @param speed текущая скорость в км/ч
 * @param averageSpeed средняя скорость в км/ч
 */
data class RideDataModel (val distance: Float, val speed: Float, val averageSpeed: Float)
