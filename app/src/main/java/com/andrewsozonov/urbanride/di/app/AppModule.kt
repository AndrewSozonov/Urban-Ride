package com.andrewsozonov.urbanride.di.app

import android.app.Application
import androidx.room.Room
import com.andrewsozonov.urbanride.database.RideDAO
import com.andrewsozonov.urbanride.database.RidingDatabase
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.repository.MainRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.andrewsozonov.urbanride.util.SchedulersProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Модуль предоставляет зависимости [BaseRepository], [ISchedulersProvider]
 *
 * @author Андрей Созонов
 */
@Module
class AppModule(var application: Application) {

    @Singleton
    @Provides
    fun provideRepository(
        rideDAO: RideDAO,
    ): BaseRepository {
        return MainRepository(rideDAO)
    }

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

    @Provides
    fun provideScheduler(): ISchedulersProvider {
        return SchedulersProvider()
    }
}