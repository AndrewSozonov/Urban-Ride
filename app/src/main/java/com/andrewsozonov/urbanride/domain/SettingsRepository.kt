package com.andrewsozonov.urbanride.domain

/**
 * Интерфейс предоставляющий доступ к настройкам
 *
 * @author Андрей Созонов
 */
interface SettingsRepository {

    /**
     * Получает единицы измерения из настроек приложения
     *
     * @return true - метры, false - мили
     */
    fun isUnitsMetric(): Boolean


    /**
     * Проверяет включена ли темная тема в настройках
     *
     * @return true - включена, false - выключена
     */
    fun isDarkThemeOn(): Boolean
}