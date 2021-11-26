package com.andrewsozonov.urbanride.domain

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.andrewsozonov.urbanride.models.data.RideDBModel
import com.andrewsozonov.urbanride.models.data.RideDataModel
import com.andrewsozonov.urbanride.models.presentation.ride.RideModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.models.presentation.service.ServiceStatus

/**
 * Интерфейс репозитория с данными о поездках
 *
 * @author Андрей Созонов
 */
interface RideRepository {

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
    fun getTimerValue(): LiveData<String>

    /**
     * Обновляет статус сервиса геолокации
     *
     * @param status STARTED, STOPPED, PAUSED
     */
    fun updateServiceStatus(status: ServiceStatus)

    /**
     * Возвращает статус сервиса геолокации
     */
    fun getServiceStatus(): LiveData<ServiceStatus>

    /**
     * Возвращает LiveData со списком координат
     */
    fun getTrackingData(): LiveData<RideDataModel>
}