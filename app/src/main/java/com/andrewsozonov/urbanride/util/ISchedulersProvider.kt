package com.andrewsozonov.urbanride.util

import io.reactivex.Scheduler

/**
 * Интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
interface ISchedulersProvider {

    /**
     * Предоставляет Scheduler для выполнения блокирующих IO операций
     */
    fun io() : Scheduler

    /**
     * Возвращает UI Scheduler
     */
    fun ui() : Scheduler
}