package com.andrewsozonov.urbanride.di.fragment

import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.presentation.history.HistoryViewModelFactory
import com.andrewsozonov.urbanride.presentation.map.MapViewModelFactory
import com.andrewsozonov.urbanride.presentation.ride.RideViewModelFactory
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставляет зависимости [RideViewModelFactory], [HistoryViewModelFactory],
 * [MapViewModelFactory]
 *
 * @author Андрей Созонов
 */

@Module
class FragmentModule {

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
    fun provideMapViewModelFactory(
        screenInteractor: MapScreenInteractor,
        schedulersProvider: ISchedulersProvider
    ): MapViewModelFactory {
        return MapViewModelFactory(screenInteractor, schedulersProvider)
    }
}