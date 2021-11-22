package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.presentation.service.LocationViewModelFactory
import com.andrewsozonov.urbanride.presentation.history.HistoryViewModelFactory
import com.andrewsozonov.urbanride.presentation.map.MapViewModelFactory
import com.andrewsozonov.urbanride.presentation.ride.RideViewModelFactory
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import dagger.Module
import dagger.Provides


/**
 * Модуль предоставляет зависимости [RideViewModelFactory], [HistoryViewModelFactory],
 * [LocationViewModelFactory], [MapViewModelFactory]
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
        repository: RideRepository,
    ): LocationViewModelFactory {
        return LocationViewModelFactory(repository)
    }

    @Provides
    fun provideMapViewModelFactory(
        screenInteractor: MapScreenInteractor,
        schedulersProvider: ISchedulersProvider
    ): MapViewModelFactory {
        return MapViewModelFactory(screenInteractor, schedulersProvider)
    }
}