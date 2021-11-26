package com.andrewsozonov.urbanride.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.SettingsRepository

/**
 * Класс для создания [MainViewModel]
 *
 * @param settingsRepository интерфейс с настройками
 *
 * @author Андрей Созонов
 */
class MainActivityViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(settingsRepository) as T
    }
}