package com.andrewsozonov.urbanride.presentation.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider

/**
 * Класс для создания [RideViewModel]
 *
 * @author Андрей Созонов
 */
class RideViewModelFactory(
    private val interactor: RideInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RideViewModel(interactor, schedulersProvider) as T
    }
}