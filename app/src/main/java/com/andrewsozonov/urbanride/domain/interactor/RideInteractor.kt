package com.andrewsozonov.urbanride.domain.interactor

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.SettingsRepository

/**
 * Интерактор экрана Ride
 *
 * @param repository ссылка на интерфейс главного репозитория
 * @param settings интерфейс с настройками
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class RideInteractor(val repository: RideRepository, private val settings: SettingsRepository, val converter: RideConverter) {

    /**
     * Получает статус сервиса геолокации из репозитория
     */
    fun getServiceStatus(): LiveData<String> = repository.getServiceStatus()

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
        Log.d("RideInteractor", " unitsMetric: ${settings.getUnits()}")
        return data.map {
            converter.convertFromRideDataModelToRideModel(
                it,
                settings.getUnits()
            )
        }
    }

}