package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.presentation.activity.MainActivityViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * Модуль предоставляет зависимости [MainActivityViewModelFactory]
 *
 * @author Андрей Созонов
 */
@Module
class ActivityModule {

    @Provides
    fun provideMainActivityViewModelFactory(
        settingsRepository: SettingsRepository,
    ): MainActivityViewModelFactory {
        return MainActivityViewModelFactory(settingsRepository)
    }
}