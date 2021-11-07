package com.andrewsozonov.urbanride.repository

import com.andrewsozonov.urbanride.database.Ride

interface BaseRepository {

    fun addRide(ride: Ride)

    fun deleteRide(ride: Ride)

    fun getAllRides() : List<Ride>
}