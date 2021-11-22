package com.andrewsozonov.urbanride.domain.interactor

import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.converter.MapScreenDataConverter
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel

/**
 * Интерактор экрана Map
 *
 * @param repository ссылка на интерфейс главного репозитория
 * @param screenDataConverter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class MapScreenInteractor(val repository: RideRepository, val screenDataConverter: MapScreenDataConverter) {

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