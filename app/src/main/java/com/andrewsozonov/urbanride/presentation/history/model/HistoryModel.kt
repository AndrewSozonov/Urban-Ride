package com.andrewsozonov.urbanride.presentation.history.model

import android.graphics.Bitmap

/**
 * Модель данных для отображения на экране History
 *
 * @param date дата поездки
 * @param startTime время начала поездки
 * @param finishTime время окончания поездки
 * @param duration длительность поездки
 * @param distance дистанция в км/ч или милях/ч
 * @param averageSpeed средняя скорость в км/ч или милях/ч
 * @param maxSpeed средняя скорость в км/ч или милях/ч
 * @param mapImg картинка с маршрутом
 * @param trackingPoints список координат
 * @param isUnitsMetric единиицы измерения из preferences true - метры, false - мили
 *
 * @author Андрей Созонов
 */
data class HistoryModel(
    val id: Int,
    val date: String,
    val startTime: String,
    val finishTime: String,
    val duration: String,
    val distance: Double,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val mapImg: Bitmap,
    val trackingPoints: List<List<HistoryLocationPoint>>,
    val isUnitsMetric: Boolean
)
