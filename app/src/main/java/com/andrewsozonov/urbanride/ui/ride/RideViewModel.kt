package com.andrewsozonov.urbanride.ui.ride

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.repository.MainRepository
import com.andrewsozonov.urbanride.util.DataFormatter
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class RideViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository
    private var disposable: Disposable? = null

    val trackingPoints: List<List<LatLng>> = mutableListOf()
    val timerLiveData: MutableLiveData<String> = MutableLiveData()
    val data: MutableLiveData<RideDataModel> = MutableLiveData()

    private var ridingTime: Long = 0L
    private var pointStartTime: Long = 0
    private var pointEndTime: Long = 0
    private var distance:Float = 0f
    private var speed: Float = 0f
    private var averageSpeed: Float = 0f

    init {
        App.getAppComponent()?.inject(this)
    }

    fun calculateTime(time: Long) {
        ridingTime = time
        timerLiveData.value = DataFormatter.formatTime(ridingTime)
    }

    fun calculateData(trackingPoints: List<List<LatLng>>) {
        distance = DataFormatter.calculateDistance(trackingPoints)
        pointEndTime = ridingTime
        speed = DataFormatter.calculateSpeed(pointEndTime - pointStartTime, trackingPoints)
        pointStartTime = pointEndTime
        averageSpeed = DataFormatter.calculateAverageSpeed(ridingTime, distance)
        data.value = RideDataModel(distance, speed, averageSpeed)
    }

    fun saveRide(mapImage: Bitmap) {
        val rideFinishTimeInMillis = Calendar.getInstance().timeInMillis
        val rideStartTimeInMillis = rideFinishTimeInMillis - ridingTime

        val currentRide = Ride(
            rideStartTimeInMillis,
            rideFinishTimeInMillis,
            ridingTime,
            distance,
            averageSpeed,
            0.0f,
            mapImage
        )
        addRideToDB(currentRide)
    }

    private fun addRideToDB(ride: Ride) {

        val observable = Completable.fromCallable {
            repository.addRide(ride)
        }

        disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

}