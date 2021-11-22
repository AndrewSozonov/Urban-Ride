package com.andrewsozonov.urbanride.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * База данных с историей поездок
 *
 * @author Андрей Созонов
 */
@Database(
    entities = [RideDBModel::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class RidingDatabase : RoomDatabase() {

    abstract fun getRideDAO(): RideDao
}