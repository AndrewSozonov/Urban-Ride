package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.presentation.service.LocationViewModelFactory
import com.andrewsozonov.urbanride.presentation.history.HistoryViewModelFactory
import com.andrewsozonov.urbanride.presentation.map.MapViewModelFactory
import com.andrewsozonov.urbanride.presentation.ride.RideViewModelFactory
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import dagger.Module
import dagger.Provides


/**
 * Модуль предоставляет зависимости [RideViewModelFactory], [HistoryViewModelFactory]
 *
 * @author Андрей Созонов
 */

@Module
class ActivityModule {

    @Provides
    fun provideRideViewModelFactory(
        repository: BaseRepository,
        schedulersProvider: ISchedulersProvider
    ): RideViewModelFactory {
        return RideViewModelFactory(repository, schedulersProvider)
    }

    @Provides
    fun provideHistoryViewModelFactory(
        repository: BaseRepository,
        schedulersProvider: ISchedulersProvider
    ): HistoryViewModelFactory {
        return HistoryViewModelFactory(repository, schedulersProvider)
    }

    @Provides
    fun provideLocationViewModelFactory(
        repository: BaseRepository,
    ): LocationViewModelFactory {
        return LocationViewModelFactory(repository)
    }

    @Provides
    fun provideMapViewModelFactory(
        repository: BaseRepository,
        schedulersProvider: ISchedulersProvider
    ): MapViewModelFactory {
        return MapViewModelFactory(repository, schedulersProvider)
    }
}