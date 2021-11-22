package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider

/**
 * Класс для создания [MapViewModel]
 *
 * @author Андрей Созонов
 */
class MapViewModelFactory(
    private val screenInteractor: MapScreenInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(screenInteractor, schedulersProvider) as T
    }
}