package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.domain.interactor.MapInteractor
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
        interactor: RideInteractor,
        schedulersProvider: ISchedulersProvider
    ): RideViewModelFactory {
        return RideViewModelFactory(interactor, schedulersProvider)
    }

    @Provides
    fun provideHistoryViewModelFactory(
        interactor: HistoryInteractor,
        schedulersProvider: ISchedulersProvider
    ): HistoryViewModelFactory {
        return HistoryViewModelFactory(interactor, schedulersProvider)
    }

    @Provides
    fun provideLocationViewModelFactory(
        repository: BaseRepository,
    ): LocationViewModelFactory {
        return LocationViewModelFactory(repository)
    }

    @Provides
    fun provideMapViewModelFactory(
        interactor: MapInteractor,
        schedulersProvider: ISchedulersProvider
    ): MapViewModelFactory {
        return MapViewModelFactory(interactor, schedulersProvider)
    }
}