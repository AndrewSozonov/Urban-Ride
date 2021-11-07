package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.ui.history.HistoryViewModelFactory
import com.andrewsozonov.urbanride.ui.ride.RideViewModelFactory
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
}