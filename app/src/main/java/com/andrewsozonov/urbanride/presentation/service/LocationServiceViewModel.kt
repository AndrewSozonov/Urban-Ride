package com.andrewsozonov.urbanride.presentation.service

import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.andrewsozonov.urbanride.repository.BaseRepository

class LocationServiceViewModel(val repository: BaseRepository) : ViewModel() {


    fun updateTimerValue(time: Long) {
        repository.updateTimerValue(time)
    }

    /*fun updateTrackingPoints(trackingPoints: MutableList<MutableList<LatLng>>) {
        repository.updateLocation(trackingPoints)
    }*/

    fun updateTrackingPoints(trackingPoints: MutableList<MutableList<LocationPoint>>) {
        repository.updateLocation(trackingPoints)
    }

    fun updateServiceStatus(status: String) {
        repository.updateServiceStatus(status)
    }
}