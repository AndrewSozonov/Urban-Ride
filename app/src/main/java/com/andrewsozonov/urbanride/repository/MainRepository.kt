package com.andrewsozonov.urbanride.repository

import android.util.Log
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.database.RideDAO
import javax.inject.Inject

class MainRepository @Inject constructor(val rideDAO: RideDAO) {

    fun addRide(ride: Ride) = rideDAO.addRide(ride)

    fun deleteRide(ride: Ride) = rideDAO.deleteRide(ride)

    fun getAllRides() : List<Ride> {
        Log.d("MainRepository", "getAllRides()")
        return rideDAO.getAllRides()
    }

}