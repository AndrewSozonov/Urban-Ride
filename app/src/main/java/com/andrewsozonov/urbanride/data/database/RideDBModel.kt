package com.andrewsozonov.urbanride.data.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint

/**
 * Модель данных для добавления в БД
 *
 * @param startTime время начала поездки в миллисекундах
 * @param finishTime время конца поездки в миллисекундах
 * @param duration длительность поездки в миллисекундах
 * @param distance расстояние поездки в метрах
 * @param averageSpeed средняя скорость в м/с
 * @param mapImg изображение с конечным маршрутом на карте
 *
 * @author Андрей Созонов
 */
@Entity(tableName = "riding_table")
data class RideDBModel(
    val startTime: Long,
    val finishTime: Long,
    val duration: Long,
    val distance: Float,
    val averageSpeed: Float,
    val mapImg: Bitmap,
    val trackingPoints: List<List<LocationPoint>>
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
