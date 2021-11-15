package com.andrewsozonov.urbanride.data.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint

/**
 * Интерфейс главного репозитория приложения
 *
 * @author Андрей Созонов
 */
interface BaseRepository {

    /**
     * Добавить поездку в БД
     *
     * @param mapImage изображение с маршрутом для сохранения
     */
    fun addRide(mapImage: Bitmap)

    /**
     * Удалить поездку из БД
     *
     * @param id id элемента для удаления
     */
    fun deleteRide(id: Int)

    /**
     * Получить список всех поездок из БД
     *
     * @return список [RideDBModel]
     */
    fun getAllRides(): List<RideDBModel>

    /**
     * Получить поездку из БД
     *
     * @param id id элемента
     * @return модель поездки [RideModel]
     */
    fun getRideById(id: Int): RideDBModel

    /**
     * Обновляет значение таймера в репозитории
     *
     * @param time время в миллисекундах
     */
    fun updateTimerValue(time: Long)


    /**
     * Обновляет список с координатами в репозитории
     *
     * @param trackingPoints список [LocationPoint]
     */
    fun updateLocation(trackingPoints: MutableList<MutableList<LocationPoint>>)

    /**
     * Возвращает значение таймера из репозитория
     *
     * @return LiveData со значением таймера
     */
    fun getTimerValue(): MutableLiveData<String>

    /**
     * Обновляет статус сервиса геолокации
     *
     * @param status STARTED, STOPPED, PAUSED
     */
    fun updateServiceStatus(status: String)

    /**
     * Возвращает статус сервиса геолокации
     */
    fun getServiceStatus(): LiveData<String>

    /**
     * Возвращает LiveData со списком координат
     */
    fun getTrackingData(): MutableLiveData<RideDataModel>
}