package com.andrewsozonov.urbanride.data.repository

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.data.database.RideDao
import com.andrewsozonov.urbanride.data.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.DataFormatter
import java.util.*


/**
 * Главный репозиторий приложения
 *
 * @param rideDao интерфейс предоставляющий доступ к БД
 * @param converter репозитория
 *
 * @author Андрей Созонов
 */
class MainRepository(
    private val rideDao: RideDao,
    private val converter: RepositoryConverter
) : BaseRepository {

    private var trackingPoints: List<List<LocationPoint>> = mutableListOf()
    private var ridingTime: Long = 0L  // миллисекунды
    private var distance: Float = 0f  // метры
    private var speed: Float = 0f // метры в сек
    private var averageSpeed: Float = 0f // метры в сек

    private val serviceStatusLiveData: MutableLiveData<String> = MutableLiveData()
    private val timerLiveData: MutableLiveData<String> = MutableLiveData()
    private val data: MutableLiveData<RideDataModel> = MutableLiveData()

    override fun getTimerValue(): LiveData<String> {
        return timerLiveData
    }

    override fun updateServiceStatus(status: String) {
        serviceStatusLiveData.value = status
    }

    override fun getServiceStatus(): LiveData<String> {
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
        val rideStartTimeInMillis = rideFinishTimeInMillis - ridingTime

        val currentRide = RideDBModel(
            rideStartTimeInMillis,
            rideFinishTimeInMillis,
            ridingTime,
            distance,
            averageSpeed,
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
        Log.d("repository", "rides: ${rideDao.getAllRides().last()}")
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
        ridingTime = time
        timerLiveData.value = DataFormatter.formatTime(ridingTime)
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
        val rideDataModel = converter.convertDataToRideDataModel(trackingPoints, ridingTime)

        distance = rideDataModel.distance
        speed = rideDataModel.speed
        averageSpeed = rideDataModel.averageSpeed
        Log.d("calculateData", "rideDataModel ${rideDataModel.trackingPoints}")
        data.value = rideDataModel
    }
}