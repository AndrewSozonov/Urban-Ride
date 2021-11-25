package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.presentation.BaseViewModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single

/**
 * [ViewModel] прикреплена к [MapFragment]
 * Загружает поездку из БД для отображения маршрута на карте
 *
 * @param interactor интерактор экрана Map
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class MapViewModel(
    val interactor: MapScreenInteractor,
    val schedulersProvider: ISchedulersProvider
    ) : BaseViewModel() {

    var trackingPoints: MutableLiveData<List<List<LatLng>>> = MutableLiveData()

    /**
     * [MutableLiveData] хранит значение загружаются ли данные в этот момент
     */
    val isLoading = MutableLiveData<Boolean>()

    /**
     * Получить поездку из БД
     *
     * @param id id элемента
     * @return модель поездки [RideModel]
     */
    fun getRide(id: Int) {
        val singleObservable = Single.fromCallable {
            interactor.getRideById(id)
        }

        compositeDisposable.add(singleObservable
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .doOnSubscribe { isLoading.value = true }
            .doAfterTerminate { isLoading.value = false }
            .subscribe({
                trackingPoints.value = it.trackingPoints
            }, {})
        )
    }
}