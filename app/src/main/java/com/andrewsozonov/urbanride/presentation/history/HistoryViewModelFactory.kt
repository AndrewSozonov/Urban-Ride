package com.andrewsozonov.urbanride.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.util.ISchedulersProvider


/**
 * Класс для создания [HistoryViewModel]
 *
 * @author Андрей Созонов
 */
class HistoryViewModelFactory(
    private val interactor: HistoryInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryViewModel(interactor, schedulersProvider) as T
    }
}