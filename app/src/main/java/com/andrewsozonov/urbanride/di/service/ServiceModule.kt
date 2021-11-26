package com.andrewsozonov.urbanride.di.service

import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.presentation.service.LocationViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставляет зависимости [LocationViewModelFactory]
 *
 * @author Андрей Созонов
 */

@Module
class ServiceModule {

    @Provides
    fun provideLocationViewModelFactory(
        repository: RideRepository,
    ): LocationViewModelFactory {
        return LocationViewModelFactory(repository)
    }
}