package com.andrewsozonov.urbanride.data.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.data.database.RideDao
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.presentation.service.model.ServiceStatus
import com.andrewsozonov.urbanride.util.DataFormatter
import java.util.*


/**
 * Реализация [RideRepository]
 * Репозиторий с данными о текущей поездке
 *
 * @param rideDao интерфейс предоставляющий доступ к БД
 * @param converterRide репозитория
 *
 * @author Андрей Созонов
 */
class RideRepositoryImpl(
    private val rideDao: RideDao,
    private val converterRide: RideRepositoryConverter
) : RideRepository {

    private var trackingPoints: List<List<LocationPoint>> = mutableListOf()
    private var ridingTimeInMillis: Long = 0L  // миллисекунды
    private var distanceInMeters: Float = 0f  // метры
    private var speedInMpS: Float = 0f // метры в сек
    private var averageSpeedInMpS: Float = 0f // метры в сек

    private val serviceStatusLiveData: MutableLiveData<ServiceStatus> = MutableLiveData()
    private val timerLiveData: MutableLiveData<String> = MutableLiveData()
    private val data: MutableLiveData<RideDataModel> = MutableLiveData()

    override fun getTimerValue(): LiveData<String> {
        return timerLiveData
    }

    override fun updateServiceStatus(status: ServiceStatus) {
        serviceStatusLiveData.value = status
    }

    override fun getServiceStatus(): LiveData<ServiceStatus> {
        return serviceStatusLiveData
    }

    override fun getTrackingData(): LiveData<RideDataModel> {
        return data
    }

    /**
     * Добавляет поездку [RideDBModel] в БД
     *
     * @param mapImage изображение карты с конечным маршрутом
     */
    override fun addRide(mapImage: Bitmap) {

        val rideFinishTimeInMillis = Calendar.getInstance().timeInMillis
        val rideStartTimeInMillis = rideFinishTimeInMillis - ridingTimeInMillis

        val currentRide = RideDBModel(
            rideStartTimeInMillis,
            rideFinishTimeInMillis,
            ridingTimeInMillis,
            distanceInMeters,
            averageSpeedInMpS,
            mapImage,
            trackingPoints
        )
        rideDao.addRide(currentRide)
    }

    /**
     * Удаляет поездку из БД
     *
     * @param id id элемента для удаления
     */
    override fun deleteRide(id: Int) = rideDao.deleteRide(id)

    /**
     * Получает список всех поездок из БД
     *
     * @return список поездок [RideDBModel]
     */
    override fun getAllRides(): List<RideDBModel> {
        return rideDao.getAllRides()
    }

    /**
     * Получает поездку из БД
     *
     * @param id id элемента
     * @return модель поездки [RideDBModel]
     */
    override fun getRideById(id: Int): RideDBModel {
        return rideDao.getRideByID(id)
    }

    /**
     * Обновляет значение таймера в репозитории
     *
     * @param time время в миллисекундах
     */
    override fun updateTimerValue(time: Long) {
        ridingTimeInMillis = time
        timerLiveData.value = DataFormatter.formatTime(ridingTimeInMillis)
    }

    /**
     * Обновляет список с координатами в репозитории
     *
     * @param trackingPoints список [LocationPoint]
     */
    override fun updateLocation(trackingPoints: MutableList<MutableList<LocationPoint>>) {

        this.trackingPoints = trackingPoints
        calculateData()
    }

    /**
     * Вычисляет из списка с координатами расстояние, скорость и среднюю скорость
     * Собирает модель данных [RideDataModel]
     * обновляет в LiveData[data]
     */
    private fun calculateData() {
        val rideDataModel = converterRide.convertDataToRideDataModel(trackingPoints, ridingTimeInMillis)

        distanceInMeters = rideDataModel.distance
        speedInMpS = rideDataModel.speed
        averageSpeedInMpS = rideDataModel.averageSpeed
        data.value = rideDataModel
    }
}