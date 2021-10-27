package com.andrewsozonov.urbanride.ui.ride

import android.util.Log
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.repository.MainRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class RideViewModel : ViewModel() {

    @Inject
    lateinit var repository: MainRepository

    init {
        App.getAppComponent()?.inject(this)
    }


    fun addRideToDB(ride: Ride) {

        val observable = Completable.fromCallable {
            Log.d("MainViewModel", "repository: $repository")

            repository.addRide(ride)
        }

        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({Log.d("RideViewModel", " addRideToDB $ride")})
    }

}