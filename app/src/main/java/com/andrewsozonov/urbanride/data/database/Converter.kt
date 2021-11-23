package com.andrewsozonov.urbanride.data.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

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
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    /**
     * Конвертирует изображение в массив байтов
     *
     * @param bitmap изображение для конвертирования
     * @return возвращает массив байтов который можно добавить в БД
     */
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Конвертирует модель [LocationPoint] в String для записи в БД
     *
     * @param list список координат [LocationPoint]
     * @return возвращает String для записи в БД
     */
    @TypeConverter
    fun fromLatLng(list: List<List<LocationPoint>>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    /**
     * Конвертирует String в модель [LocationPoint]
     *
     * @param jsonString String из БД
     * @return возвращает список координат [LocationPoint]
     */
    @TypeConverter
    fun fromStringToLatLng(jsonString: String): List<List<LocationPoint>> {

        val type: Type = object : TypeToken<ArrayList<ArrayList<LocationPoint>>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

}