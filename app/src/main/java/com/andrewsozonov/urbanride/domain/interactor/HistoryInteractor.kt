package com.andrewsozonov.urbanride.domain.interactor

import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.converter.HistoryConverter
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel

/**
 * Интерактор экрана History
 *
 * @param repository ссылка на интерфейс главного репозитория
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class HistoryInteractor(val repository: BaseRepository, val converter: HistoryConverter) {

    /**
     * Удаляет поездку из БД по id
     *
     * @param id id поездки
     */
    fun deleteRide(id: Int) {
        repository.deleteRide(id)
    }

    /**
     * Получает список всех поездок из БД
     * конвертирует их в зависимости от единиц измерения [HistoryModel]
     *
     * @param isUnitsMetric единицы измерения из preferences
     */
     fun getAllRides(isUnitsMetric: Boolean) : List<HistoryModel> {
         return repository.getAllRides().map { converter.convertFromRideToHistoryModel(it, isUnitsMetric) }
     }
}