package com.andrewsozonov.urbanride.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.presentation.history.HistoryViewModel

/**
 * Класс для создания [LocationServiceViewModel]
 *
 * @author Андрей Созонов
 */
class LocationViewModelFactory (
    private val repository: BaseRepository,
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LocationServiceViewModel(repository) as T
    }
}