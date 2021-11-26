package com.andrewsozonov.urbanride.presentation.activity

import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.presentation.BaseViewModel

/**
 * [ViewModel] прикреплена к [MainActivity]
 * Загружает настройки приложения
 *
 * @param settingsRepository интерфейс с настройками
 *
 * @author Андрей Созонов
 */

class MainViewModel(val settingsRepository: SettingsRepository) : BaseViewModel() {

    fun isDarkThemeOn() = settingsRepository.isDarkThemeOn()
}