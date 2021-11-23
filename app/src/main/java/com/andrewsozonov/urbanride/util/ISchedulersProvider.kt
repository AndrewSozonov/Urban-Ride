package com.andrewsozonov.urbanride.util

import io.reactivex.Scheduler

/**
 * Интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
interface ISchedulersProvider {

    fun io() : Scheduler

    fun ui() : Scheduler
}