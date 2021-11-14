package com.andrewsozonov.urbanride.presentation.history.model

import android.graphics.Bitmap

data class HistoryModel(
    var id: Int,
    var date: String,
    var startTime: String,
    var finishTime: String,
    var duration: String,
    var distance: Double,
    var averageSpeed: Double,
    var maxSpeed: Float,
    var mapImg: Bitmap,
    var trackingPoints: List<List<HistoryLocationPoint>>
)
