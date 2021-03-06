package com.andrewsozonov.urbanride.domain.interactor

import com.andrewsozonov.urbanride.domain.RideRepository
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.domain.converter.HistoryConverter
import com.andrewsozonov.urbanride.models.presentation.history.HistoryModel

/**
 * Интерактор экрана History
 *
 * @param repository интерфейс репозитория с данными о поездках
 * @param settings интерфейс с настройками
 * @param converter конвертер модели данных
 *
 * @author Андрей Созонов
 */
class HistoryInteractor(
    private val repository: RideRepository,
    private val settings: SettingsRepository,
    private val converter: HistoryConverter
) {

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
     */
    fun getAllRides(): List<HistoryModel> {
        return repository.getAllRides()
            .map { converter.convertFromRideToHistoryModel(it, settings.isUnitsMetric()) }
    }
}