package com.andrewsozonov.urbanride.presentation.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * [ViewModel] прикреплена к [HistoryFragment]
 * Загружает список поездок из БД для отображения во фрагменте
 *
 * @param repository интерфейс репозитория
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class HistoryViewModel(val repository: BaseRepository, val schedulersProvider: ISchedulersProvider) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * [MutableLiveData] хранит модель данных для отображения во фрагменте
     */
    val listOfRides = MutableLiveData<List<Ride>>()

    /**
     * Удаляет поездку из БД
     *
     * @param ride модель для удаления
     */
    fun deleteRide(ride: Ride) {
        val observable = Completable.fromCallable {
            repository.deleteRide(ride)
        }
        compositeDisposable.add(observable
            .andThen(
                Single.fromCallable { repository.getAllRides() }
            )
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { t1, t2 ->
                t1?.let(listOfRides::setValue)
                Log.d("getRidesFromDB", " listOfRides $t1")

            })
    }

    /**
     * Получает список поездок [Ride] из БД
     */
    fun getRidesFromDB() {
        val singleObservable = Single.fromCallable {
            repository.getAllRides()
        }
        compositeDisposable.add(singleObservable
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe({
                listOfRides.value = it
            }, {})
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}