package com.andrewsozonov.urbanride.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * Конвертирует данные для БД
 *
 * @author Андрей Созонов
 */
class Converter {

    /**
     * Ковертирует из массива байтов в изображение
     *
     * @param byteArray массив байтов полученный из БД
     * @return возвращает изображение [Bitmap]
     */
    @TypeConverter
    fun toBitmap(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    /**
     * Конвертирует изображение в массив байтов
     *
     * @param bitmap изображение для конвертирования
     * @return возвращает массив байтов который можно добавить в БД
     */
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}