package com.andrewsozonov.urbanride.domain.interactor

import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.converter.MapScreenDataConverter
import com.andrewsozonov.urbanride.models.presentation.ride.RideModel

/**
 * Интерактор экрана Map
 *
 * @param repository интерфейс репозитория с данными о поездках
 * @param screenDataConverter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class MapScreenInteractor(
    val repository: RideRepository,
    val screenDataConverter: MapScreenDataConverter
) {

    /**
     * Получает поездку из БД
     *
     * @param id id элемента
     * @return модель поездки [RideModel]
     */
    fun getRideById(id: Int): RideModel {
        return screenDataConverter.convertFromRideDBModelToRideModel(repository.getRideById(id))
    }
}