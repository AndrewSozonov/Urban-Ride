package com.andrewsozonov.urbanride.app

import android.app.Application
import androidx.room.Room
import com.andrewsozonov.urbanride.database.RideDAO
import com.andrewsozonov.urbanride.database.RidingDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(var application: Application) {

    @Singleton
    @Provides
    fun provideRideDao(): RideDAO {
        val appDatabase: RidingDatabase = Room.databaseBuilder(
            application.applicationContext,
            RidingDatabase::class.java,
            "room_database"
        ).build()
        return appDatabase.getRideDAO()
    }
}