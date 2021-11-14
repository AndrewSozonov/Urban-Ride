package com.andrewsozonov.urbanride.domain.interactor

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.andrewsozonov.urbanride.domain.converter.RideConverter
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.data.repository.BaseRepository

/**
 * Интерактор экрана Ride
 *
 * @param repository ссылка на интерфейс главного репозитория
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class RideInteractor(val repository: BaseRepository, val converter: RideConverter) {

    private var isUnitsMetric: Boolean = true

    /**
     * Устанавливает значение единиц измерения мили или км
     * полученное из preferences
     *
     * @param isUnitsMetric если true - км, false - мили
     */
    fun setUnits(isUnitsMetric: Boolean) {
        this.isUnitsMetric = isUnitsMetric
    }

    /**
     * Получает статус сервиса геолокации из репозитория
     */
    fun getServiceStatus(): LiveData<String> = repository.getServiceStatus()

    /**
     * Получает значение таймера из репозитория
     */
    fun getTimerValue(): MutableLiveData<String> = repository.getTimerValue()

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
        val domainData = repository.getTrackingData()
        return domainData.map {
            converter.convertFromRideDataModelToRideModel(
                it,
                isUnitsMetric
            )
        }
    }

}