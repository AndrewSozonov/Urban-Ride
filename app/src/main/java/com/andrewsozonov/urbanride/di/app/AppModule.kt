package com.andrewsozonov.urbanride.di.app

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.andrewsozonov.urbanride.data.database.RideDao
import com.andrewsozonov.urbanride.data.database.RidingDatabase
import com.andrewsozonov.urbanride.data.repository.*
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.domain.converter.HistoryConverter
import com.andrewsozonov.urbanride.domain.converter.MapScreenDataConverter
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.andrewsozonov.urbanride.util.SchedulersProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Модуль предоставляет зависимости [RideRepository], [ISchedulersProvider], [RideDao],
 * [RideRepositoryConverter], [RideInteractor], [RideConverter], [HistoryInteractor], [HistoryConverter],
 * [MapScreenInteractor], [MapScreenDataConverter], [SettingsRepository], [SharedPreferences]
 *
 * @author Андрей Созонов
 */
@Module
class AppModule(var application: Application) {

    @Singleton
    @Provides
    fun provideRepository(
        rideDao: RideDao, rideRepositoryConverter: RideRepositoryConverter
    ): RideRepository {
        return RideRepositoryImpl(rideDao, rideRepositoryConverter)
    }

    @Singleton
    @Provides
    fun provideRideDao(): RideDao {
        val appDatabase: RidingDatabase = Room.databaseBuilder(
            application.applicationContext,
            RidingDatabase::class.java,
            "room_database"
        ).build()
        return appDatabase.getRideDAO()
    }

    @Provides
    fun provideRepositoryConverter(): RideRepositoryConverter {
        return RideRepositoryConverter()
    }

    @Provides
    fun provideScheduler(): ISchedulersProvider {
        return SchedulersProvider()
    }

    @Provides
    fun provideRideInteractor(repository: RideRepository, settings: SettingsRepository, converter: RideConverter): RideInteractor {
        return RideInteractor(repository, settings, converter)
    }

    @Provides
    fun providePresentationConverter(): RideConverter {
        return RideConverter()
    }

    @Provides
    fun provideHistoryInteractor(repository: RideRepository, settings: SettingsRepository, converter: HistoryConverter): HistoryInteractor {
        return HistoryInteractor(repository, settings, converter)
    }

    @Provides
    fun provideHistoryConverter(): HistoryConverter {
        return HistoryConverter()
    }

    @Provides
    fun provideMapScreenInteractor(repository: RideRepository, screenDataConverter: MapScreenDataConverter): MapScreenInteractor {
        return MapScreenInteractor(repository, screenDataConverter)
    }

    @Provides
    fun provideMapScreenConverter(): MapScreenDataConverter {
        return MapScreenDataConverter()
    }

    @Provides
    fun provideSettingsRepository(sharedPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }

    @Provides
    fun provideSharedPrefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}