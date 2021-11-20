package com.andrewsozonov.urbanride.presentation.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.presentation.BaseViewModel
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import io.reactivex.Completable
import io.reactivex.Single


/**
 * [ViewModel] прикреплена к [HistoryFragment]
 * Загружает список поездок из БД для отображения во фрагменте
 *
 * @param interactor ссылка на интерактор экрана History
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class HistoryViewModel(
    val interactor: HistoryInteractor,
    val schedulersProvider: ISchedulersProvider
) : BaseViewModel() {

    /**
     * [MutableLiveData] хранит модель данных для отображения во фрагменте
     */
    val listOfRides = MutableLiveData<List<HistoryModel>>()


    /**
     * [MutableLiveData] хранит значение загружаются ли данные в этот момент
     */
    val isLoading = MutableLiveData<Boolean>()

    /**
     * Удаляет поездку из БД
     *
     * @param id id элемента для удаления
     * @param isUnitsMetric единицы измерения переданные из preferences
     */
    fun deleteRide(id: Int, isUnitsMetric: Boolean) {
        val observable = Completable.fromCallable {
            interactor.deleteRide(id)
        }
        compositeDisposable.add(observable
            .doOnSubscribe { isLoading.postValue(true) }
            .andThen(
                Single.fromCallable { interactor.getAllRides(isUnitsMetric) }
            )
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .doAfterTerminate { isLoading.value = false }
            .subscribe { t1, t2 ->
                t1?.let(listOfRides::setValue)
                Log.d("getRidesFromDB", " listOfRides $t1")

            })
    }

    /**
     * Получает список поездок [HistoryModel] из БД
     * передает в liveData [listOfRides]
     *
     * @param isUnitsMetric единицы измерения переданные из preferences
     */
    fun getRidesFromDB(isUnitsMetric: Boolean) {
        val singleObservable = Single.fromCallable {
            interactor.getAllRides(isUnitsMetric)
        }
        compositeDisposable.add(
            singleObservable
                .doOnSubscribe { isLoading.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .doAfterTerminate { isLoading.value = false }
                .subscribe({
                    listOfRides.value = it
                }, {})
        )
    }
}