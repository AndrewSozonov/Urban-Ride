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
 * Модуль предоставляет зависимости [RideRepository], [ISchedulersProvider]
 *
 * @author Андрей Созонов
 */
@Module
class AppModule(var application: Application) {

    @Singleton
    @Provides
    fun provideRepository(
        rideDao: RideDao, repositoryConverter: RepositoryConverter
    ): RideRepository {
        return RideRepositoryImpl(rideDao, repositoryConverter)
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
    fun provideRepositoryConverter(): RepositoryConverter {
        return RepositoryConverter()
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
    fun provideMapInteractor(repository: RideRepository, screenDataConverter: MapScreenDataConverter): MapScreenInteractor {
        return MapScreenInteractor(repository, screenDataConverter)
    }

    @Provides
    fun provideMapConverter(): MapScreenDataConverter {
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