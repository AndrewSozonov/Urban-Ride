package com.andrewsozonov.urbanride.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider


/**
 * Класс для создания [HistoryViewModel]
 *
 * @author Андрей Созонов
 */
class HistoryViewModelFactory(
    private val repository: BaseRepository,
    private val schedulersProvider: ISchedulersProvider
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryViewModel(repository, schedulersProvider) as T
    }
}