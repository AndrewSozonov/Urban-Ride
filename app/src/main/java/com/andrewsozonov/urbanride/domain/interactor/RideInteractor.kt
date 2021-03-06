package com.andrewsozonov.urbanride.domain.interactor

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.models.presentation.ride.RideModel
import com.andrewsozonov.urbanride.models.presentation.service.ServiceStatus

/**
 * Интерактор экрана Ride
 *
 * @param repository интерфейс репозитория с данными о поездках
 * @param settings интерфейс с настройками
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class RideInteractor(
    private val repository: RideRepository,
    private val settings: SettingsRepository,
    private val converter: RideConverter
) {

    /**
     * Получает статус сервиса геолокации из репозитория
     */
    fun getServiceStatus(): LiveData<ServiceStatus> = repository.getServiceStatus()

    /**
     * Получает значение таймера из репозитория
     */
    fun getTimerValue(): LiveData<String> = repository.getTimerValue()

    /**
     * Добавить поездку в БД
     *
     * @param mapImage картинка с маршрутом
     */
    fun addRide(mapImage: Bitmap) = repository.addRide(mapImage)

    /**
     * Получает модель данных для отображения во фрагменте из репозитория
     */
    fun getTrackingData(): LiveData<RideModel> {
        val data = repository.getTrackingData()
        return data.map {
            converter.convertFromRideDataModelToRideModel(
                it,
                settings.isUnitsMetric()
            )
        }
    }
}