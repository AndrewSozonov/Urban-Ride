package com.andrewsozonov.urbanride.di.app

import android.app.Application
import androidx.room.Room
import com.andrewsozonov.urbanride.data.database.RideDAO
import com.andrewsozonov.urbanride.data.database.RidingDatabase
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.data.repository.RepositoryConverter
import com.andrewsozonov.urbanride.data.repository.MainRepository
import com.andrewsozonov.urbanride.domain.converter.HistoryConverter
import com.andrewsozonov.urbanride.domain.converter.MapConverter
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.domain.interactor.MapInteractor
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
        rideDAO: RideDAO, repositoryConverter: RepositoryConverter
    ): BaseRepository {
        return MainRepository(rideDAO, repositoryConverter)
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
    fun provideConverter(): RepositoryConverter {
        return RepositoryConverter()
    }

    @Provides
    fun provideScheduler(): ISchedulersProvider {
        return SchedulersProvider()
    }

    @Provides
    fun provideRideInteractor(repository: BaseRepository, converter: RideConverter): RideInteractor {
        return RideInteractor(repository, converter)
    }

    @Provides
    fun providePresentationConverter(): RideConverter {
        return RideConverter()
    }

    @Provides
    fun provideHistoryInteractor(repository: BaseRepository, converter: HistoryConverter): HistoryInteractor {
        return HistoryInteractor(repository, converter)
    }

    @Provides
    fun provideHistoryConverter(): HistoryConverter {
        return HistoryConverter()
    }

    @Provides
    fun provideMapInteractor(repository: BaseRepository, converter: MapConverter): MapInteractor {
        return MapInteractor(repository, converter)
    }

    @Provides
    fun provideMapConverter(): MapConverter {
        return MapConverter()
    }
}