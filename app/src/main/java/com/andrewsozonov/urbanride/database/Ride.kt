package com.andrewsozonov.urbanride.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riding_table")
data class Ride(
    var startTime: Long,
    var finishTime: Long,
    var duration: Long,
    var distance: Float,
    var averageSpeed: Float,
    var maxSpeed: Float,
    var mapImg: Bitmap
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
