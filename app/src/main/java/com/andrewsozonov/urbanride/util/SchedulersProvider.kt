package com.andrewsozonov.urbanride.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Иплементация [ISchedulersProvider]
 *
 * @author Андрей Созонов
 */
open class SchedulersProvider: ISchedulersProvider {

    /**
     * Возвращает Scheduler для выполнения блокирующих IO операций
     */
    override fun io(): Scheduler {
        return Schedulers.io()
    }

    /**
    * Возвращает main поток Android
    */
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}