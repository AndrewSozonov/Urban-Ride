package com.andrewsozonov.urbanride.data.repository

import android.content.SharedPreferences
import com.andrewsozonov.urbanride.domain.SettingsRepository
import com.andrewsozonov.urbanride.util.constants.SettingsConstants.UNITS_KEY
import com.andrewsozonov.urbanride.util.constants.SettingsConstants.UNITS_KILOMETERS

/**
 * Реализация [SettingsRepository] на SharedPreferences
 *
 * @param sharedPreferences preferences приложения
 *
 * @author Андрей Созонов
 */
class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    /**
     * Получает единицы измерения из настроек приложения
     *
     * @return true - метры, false - мили
     */
    override fun isUnitsMetric(): Boolean {
        return sharedPreferences.getString(
            UNITS_KEY,
            UNITS_KILOMETERS
        ) == UNITS_KILOMETERS
    }
}