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
     */
    fun deleteRide(id: Int) {
        val observable = Completable.fromCallable {
            interactor.deleteRide(id)
        }
        compositeDisposable.add(observable
            .andThen(
                Single.fromCallable { interactor.getAllRides() }
            )
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .doOnSubscribe { isLoading.value = true }
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
     */
    fun getRidesFromDB() {
        val singleObservable = Single.fromCallable {
            interactor.getAllRides()
        }
        compositeDisposable.add(
            singleObservable
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .doOnSubscribe { isLoading.value = true }
                .doAfterTerminate { isLoading.value = false }
                .subscribe({
                    listOfRides.value = it
                }, {})
        )
    }
}