package com.andrewsozonov.urbanride.database

import androidx.room.*

@Dao
interface RideDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRide(ride: Ride)

    @Delete
    fun deleteRide(ride: Ride)

    @Query("SELECT * FROM riding_table")
    fun getAllRides(): List<Ride>
}