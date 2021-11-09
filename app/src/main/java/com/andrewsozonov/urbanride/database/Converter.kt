package com.andrewsozonov.urbanride.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
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

    @TypeConverter
    fun fromLatLng(list : List<List<LatLng>>) : String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToLatLng(jsonString: String) : List<List<LatLng>> {
        val gson = Gson()

        val type: Type = object : TypeToken<ArrayList<ArrayList<LatLng>>>(){}.type
        val trackingPoints: List<List<LatLng>> = Gson().fromJson(jsonString, type)

        return trackingPoints
    }

    }