package com.andrewsozonov.urbanride.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * Модель данных для репозитория
 *
 * @param distance пройденное расстояние в метрах
 * @param speed текущая скорость в м/c
 * @param averageSpeed средняя скорость в м/c
 * @param trackingPoints список координат
 */
data class RideDataModel(val distance: Float, val speed: Float, val averageSpeed: Float, val trackingPoints: List<List<LatLng>>)
