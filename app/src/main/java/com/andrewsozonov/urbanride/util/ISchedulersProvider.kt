package com.andrewsozonov.urbanride.util

import io.reactivex.Scheduler

/**
 * Интерфейс для выбора потоков
 *
 * @author Андрей Созонов
 */
interface ISchedulersProvider {

    fun io() : Scheduler

    fun ui() : Scheduler
}