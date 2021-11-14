package com.andrewsozonov.urbanride.presentation.ride.model

import com.google.android.gms.maps.model.LatLng


/**
 * Модель данных для отображения в главном фрагменте
 *
 * @param distance пройденное расстояние в км или милях в зависимости от preferences
 * @param speed текущая скорость в км/ч или или мили/ч
 * @param averageSpeed средняя скорость в км/ч или милях
 * @param trackingPoints список координат для отображения на карте
 */
data class RideModel (val distance: Float, val speed: Float, val averageSpeed: Float, val trackingPoints: List<List<LatLng>>)
