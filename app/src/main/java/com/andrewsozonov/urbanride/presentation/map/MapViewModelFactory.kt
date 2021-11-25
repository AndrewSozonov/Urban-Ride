package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider

/**
 * Класс для создания [MapViewModel]
 *
 * @param interactor интерактор экрана Map
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class MapViewModelFactory(
    private val interactor: MapScreenInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(interactor, schedulersProvider) as T
    }
}