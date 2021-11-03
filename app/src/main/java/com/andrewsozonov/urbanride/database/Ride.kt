package com.andrewsozonov.urbanride.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Модель данных для добавления в БД
 *
 * @param startTime время начала поездки в миллисекундах
 * @param finishTime время конца поездки в миллисекундах
 * @param duration длительность поездки в миллисекундах
 * @param distance расстояние поездки в км
 * @param averageSpeed средняя скорость в км/ч
 * @param maxSpeed максимальная скорость в км/ч
 * @param mapImg изображение с конечным маршрутом на карте
 *
 * @author Андрей Созонов
 */
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
