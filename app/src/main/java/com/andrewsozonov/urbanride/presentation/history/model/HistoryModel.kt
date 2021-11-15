package com.andrewsozonov.urbanride.presentation.history.model

import android.graphics.Bitmap

/**
 * Модель данных дя отображения на экране History
 *
 * @param date дата поездки
 * @param startTime время начала поездки
 * @param finishTime время окончания поездки
 * @param duration длительность поездки
 * @param distance дистанция в км/ч или милях/ч
 * @param averageSpeed средняя скорость в км/ч или милях/ч
 * @param mapImg картинка с маршрутом
 * @param trackingPoints список координат
 *
 * @author Андрей Созонов
 */
data class HistoryModel(
    var id: Int,
    var date: String,
    var startTime: String,
    var finishTime: String,
    var duration: String,
    var distance: Double,
    var averageSpeed: Double,
    var maxSpeed: Double,
    var mapImg: Bitmap,
    var trackingPoints: List<List<HistoryLocationPoint>>
)
