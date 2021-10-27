package com.andrewsozonov.urbanride.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.repository.MainRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HistoryViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    val listOfRides =  MutableLiveData<List<Ride>>()


   init {
        App.getAppComponent()?.inject(this)
    }

    fun deleteRide(ride: Ride) {
        val observable = Completable.fromCallable {
            repository.deleteRide(ride)
        }
        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("HistoryViewModel", "rideDeleted: $ride")
                getRidesFromDB()
            }
    }

    fun getRidesFromDB() {
        val singleObservable = Single.fromCallable {
            Log.d("HistoryViewModel", "getRidesFromDB")
            Log.d("HistoryViewModel", "repository: $repository")
            repository.getAllRides()
        }

        val disposable = singleObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ( {
                listOfRides.value = it
                Log.d("HistoryViewModel", " ridesFromDB ${it.toString()}")
                         }, {} )
    }
}