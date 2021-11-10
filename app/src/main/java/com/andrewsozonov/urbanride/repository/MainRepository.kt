package com.andrewsozonov.urbanride.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.database.RideDAO
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.andrewsozonov.urbanride.util.Converter
import com.andrewsozonov.urbanride.util.DataFormatter
import com.google.android.gms.maps.model.LatLng
import java.util.*
import javax.inject.Inject


/**
 * Главный репозиторий приложения
 *
 * @param rideDAO интерфейс предоставляющий доступ к БД
 *
 * @author Андрей Созонов
 */
class MainRepository @Inject constructor(private val rideDAO: RideDAO) : BaseRepository {

//    private var trackingPoints: List<List<LatLng>> = mutableListOf()
    private var trackingPoints: List<List<LocationPoint>> = mutableListOf()


    private var ridingTime: Long = 0L
    private var pointStartTime: Long = 0
    private var pointEndTime: Long = 0
    private var distance:Float = 0f
    private var speed: Float = 0f
    private var averageSpeed: Float = 0f

    private val serviceStatusLiveData: MutableLiveData<String> = MutableLiveData()
    private val timerLiveData: MutableLiveData<String> = MutableLiveData()
    private val data: MutableLiveData<RideDataModel> = MutableLiveData()
    private val locationLiveData: MutableLiveData<List<List<LatLng>>> = MutableLiveData()
//    private val locationLiveData: MutableLiveData<List<List<LocationPoint>>> = MutableLiveData()


    override fun getTimerValue(): MutableLiveData<String> {
        return  timerLiveData
    }

    override fun updateServiceStatus(status: String) {
        serviceStatusLiveData.value = status
    }

    override fun getServiceStatus(): LiveData<String> {
        return serviceStatusLiveData
    }

    override fun getTrackingPoints(): MutableLiveData<List<List<LatLng>>> {
        return locationLiveData
    }

    /*override fun getTrackingPoints(): MutableLiveData<List<List<LocationPoint>>> {
        return locationLiveData
    }*/

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
            trackingPoints)
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
    override fun getAllRides() : List<Ride> {
        return rideDAO.getAllRides()
    }

    override fun getRideById(id: Int): Ride {
        return rideDAO.getRideByID(id)
    }

    override fun updateTimerValue(time: Long) {
        ridingTime = time
        timerLiveData.value = DataFormatter.formatTime(ridingTime)
    }

    /*override fun updateLocation(trackingPoints: MutableList<MutableList<LatLng>>) {
        this.trackingPoints = trackingPoints
        locationLiveData.value = this.trackingPoints
        calculateData(trackingPoints)
    }*/

    override fun updateLocation(trackingPoints: MutableList<MutableList<LocationPoint>>) {
        this.trackingPoints = trackingPoints
//        locationLiveData.value = this.trackingPoints
        locationLiveData.value = Converter.convertListLocationPointToListLatLng(trackingPoints)
        calculateData(trackingPoints)
    }

    /**
     * Вычисляет из списка с координатами расстояние, скорость и среднюю скорость
     * обновляет в [data]
     *
     * @param trackingPoints список координат
     */
    /*private fun calculateData(trackingPoints: List<List<LatLng>>) {
        this.trackingPoints = trackingPoints
        distance = DataFormatter.calculateDistance(trackingPoints)
        pointEndTime = ridingTime
        speed = DataFormatter.calculateSpeed(pointEndTime - pointStartTime, trackingPoints)
        pointStartTime = pointEndTime
        averageSpeed = DataFormatter.calculateAverageSpeed(ridingTime, distance)
        data.value = RideDataModel(distance, speed, averageSpeed)
    }*/

    private fun calculateData(trackingPoints: List<List<LocationPoint>>) {
        this.trackingPoints = trackingPoints
        distance = DataFormatter.calculateDistance(trackingPoints)
        pointEndTime = ridingTime
//        speed = DataFormatter.calculateSpeed(pointEndTime - pointStartTime, trackingPoints)
//        speed = trackingPoints.last().last().speed


        speed = if (trackingPoints.last().isNotEmpty()) trackingPoints.last().last().speed / 1000 * 3600 else 0f

        pointStartTime = pointEndTime
        averageSpeed = DataFormatter.calculateAverageSpeed(ridingTime, distance)
        data.value = RideDataModel(distance, speed, averageSpeed, Converter.convertListLocationPointToListLatLng(trackingPoints) )
    }
}