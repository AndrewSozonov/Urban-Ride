package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.interactor.MapInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider

class MapViewModelFactory(
    private val interactor: MapInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(interactor, schedulersProvider) as T
    }
}