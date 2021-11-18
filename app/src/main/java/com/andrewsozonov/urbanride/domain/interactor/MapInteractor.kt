package com.andrewsozonov.urbanride.domain.interactor

import android.util.Log
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.converter.MapConverter
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel

/**
 * Интерактор экрана Map
 *
 * @param repository ссылка на интерфейс главного репозитория
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class MapInteractor(val repository: BaseRepository, val converter: MapConverter) {

    /**
     * Получает поездку из БД
     *
     * @param id id элемента
     * @return модель поездки [RideModel]
     */
    fun getRideById(id: Int): RideModel {
        return converter.convertFromRideDBModelToRideModel(repository.getRideById(id))
    }
}