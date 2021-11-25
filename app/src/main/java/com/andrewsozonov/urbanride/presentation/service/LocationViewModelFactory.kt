package com.andrewsozonov.urbanride.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.domain.RideRepository

/**
 * Класс для создания [LocationServiceViewModel]
 * @param repository интерфейс репозитория с данными о поездках
 *
 * @author Андрей Созонов
 */
class LocationViewModelFactory (
    private val repository: RideRepository,
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LocationServiceViewModel(repository) as T
    }
}