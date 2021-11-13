package com.andrewsozonov.urbanride.repository

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.database.LocationPointDB
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.database.RideDAO
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.andrewsozonov.urbanride.util.DataFormatter
import java.util.*
import javax.inject.Inject


/**
 * Главный репозиторий приложения
 *
 * @param rideDAO интерфейс предоставляющий доступ к БД
 *
 * @author Андрей Созонов
 */
class MainRepository @Inject constructor(private val rideDAO: RideDAO, private val converter: Converter) : BaseRepository {

    private var trackingPoints: List<List<LocationPoint>> = mutableListOf()
    private var ridingTime: Long = 0L
    private var distance: Float = 0f
    private var speed: Float = 0f
    private var averageSpeed: Float = 0f

    private val serviceStatusLiveData: MutableLiveData<String> = MutableLiveData()
    private val timerLiveData: MutableLiveData<String> = MutableLiveData()
    private val data: MutableLiveData<RideDataModel> = MutableLiveData()

    override fun getTimerValue(): MutableLiveData<String> {
        return timerLiveData
    }

    override fun updateServiceStatus(status: String) {
        serviceStatusLiveData.value = status
    }

    override fun getServiceStatus(): LiveData<String> {
        return serviceStatusLiveData
    }

    override fun getTrackingData(): MutableLiveData<RideDataModel> {
        return data
    }

    /**
     * Добавляет поездку в БД
     *
     * @param mapImage изображение карты с конечным маршрутом
     */
    override fun addRide(mapImage: Bitmap) {

        val rideFinishTimeInMillis = Calendar.getInstance().timeInMillis
        val rideStartTimeInMillis = rideFinishTimeInMillis - ridingTime

        val currentRide = Ride(
            rideStartTimeInMillis,
            rideFinishTimeInMillis,
            ridingTime,
            distance,
            averageSpeed,
            0.0f,
            mapImage,
            converter.convertSpeedOfList(trackingPoints)
        )
        rideDAO.addRide(currentRide)
    }

    /**
     * Удаляет поездку из БД
     *
     * @param ride модель данных поездки [Ride]
     */
    override fun deleteRide(ride: Ride) = rideDAO.deleteRide(ride)

    /**
     * Получает список всех поездок из БД
     *
     * @return спиок поездок [Ride]
     */
    override fun getAllRides(): List<Ride> {
        return rideDAO.getAllRides()
    }

    override fun getRideById(id: Int): RideDataModel {
        return converter.convertFromRideToRideDataModel(rideDAO.getRideByID(id))
    }

    override fun updateTimerValue(time: Long) {
        ridingTime = time
        timerLiveData.value = DataFormatter.formatTime(ridingTime)
    }

    override fun updateLocation(trackingPoints: MutableList<MutableList<LocationPoint>>) {

        this.trackingPoints = trackingPoints
        calculateData()
    }

    /**
     * Вычисляет из списка с координатами расстояние, скорость и среднюю скорость
     * обновляет в [data]
     *
     * @param trackingPoints список координат
     */


    private fun calculateData() {
        val rideDataModel = converter.convertDataToRideDataModel(trackingPoints, ridingTime)
        distance = rideDataModel.distance
        speed = rideDataModel.speed
        averageSpeed = rideDataModel.averageSpeed
        if (trackingPoints.last().isNotEmpty()) {
            trackingPoints.last().last().distance = distance
            Log.d("calculate data", " distance: ${trackingPoints.last().last().distance}")
        }
        Log.d("calculate data", " speed: $speed")
        data.value = rideDataModel
    }
}