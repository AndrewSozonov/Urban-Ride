package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.presentation.history.HistoryViewModel
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider

class MapViewModelFactory(
    private val repository: BaseRepository,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(repository, schedulersProvider) as T
    }
}