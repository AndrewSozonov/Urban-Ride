package com.andrewsozonov.urbanride.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Ride::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class RidingDatabase : RoomDatabase() {

    abstract fun getRideDAO(): RideDAO
}