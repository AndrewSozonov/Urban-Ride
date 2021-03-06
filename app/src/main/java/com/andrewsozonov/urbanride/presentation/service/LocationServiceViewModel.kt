package com.andrewsozonov.urbanride.presentation.service

import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.models.presentation.service.ServiceStatus

/**
 * [ViewModel] [LocationService]
 * Обновляет данные геолокации из сервиса в репозиторий
 *
 * @param repository интерфейс репозитория с данными о поездках
 *
 * @author Андрей Созонов
 */
class LocationServiceViewModel(val repository: RideRepository) : ViewModel() {


    /**
     * Обновляет значение таймера
     *
     * @param time значение таймера в миллисекундах
     */
    fun updateTimerValue(time: Long) {
        repository.updateTimerValue(time)
    }

    /**
     * Обновляет список координат
     */
    fun updateTrackingPoints(trackingPoints: MutableList<MutableList<LocationPoint>>) {
        repository.updateLocation(trackingPoints)
    }

    /**
     * Обновляет значение статуса сервиса геолокации
     *
     * @param status статус сервиса: STARTED, STOPPED, PAUSED
     */
    fun updateServiceStatus(status: ServiceStatus) {
        repository.updateServiceStatus(status)
    }
}