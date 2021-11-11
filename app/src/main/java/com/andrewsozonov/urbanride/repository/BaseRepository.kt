package com.andrewsozonov.urbanride.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.google.android.gms.maps.model.LatLng

interface BaseRepository {

    fun addRide(mapImage: Bitmap)

    fun deleteRide(ride: Ride)

    fun getAllRides() : List<Ride>

    fun getRideById(id: Int) : Ride

    fun updateTimerValue(time: Long)

    fun updateLocation(trackingPoints: MutableList<MutableList<LocationPoint>>)

    fun getTimerValue() : MutableLiveData<String>

    fun updateServiceStatus(status: String)

    fun getServiceStatus(): LiveData<String>

    fun getTrackingData() : MutableLiveData<RideDataModel>
}