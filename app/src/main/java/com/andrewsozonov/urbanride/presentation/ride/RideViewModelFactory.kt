package com.andrewsozonov.urbanride.presentation.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider

/**
 * Класс для создания [RideViewModel]
 *
 * @author Андрей Созонов
 */
class RideViewModelFactory(
    private val repository: BaseRepository,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RideViewModel(repository, schedulersProvider) as T
    }
}