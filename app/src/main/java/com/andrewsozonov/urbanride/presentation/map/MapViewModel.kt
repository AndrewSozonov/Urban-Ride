package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.data.repository.BaseRepository
import com.andrewsozonov.urbanride.domain.interactor.MapInteractor
import com.andrewsozonov.urbanride.presentation.history.HistoryFragment
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * [ViewModel] прикреплена к [MapFragment]
 * Загружает поездку из БД для отображения маршрута на карте
 *
 * @param interactor ссылка на интерактор экрана History
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class MapViewModel(val interactor: MapInteractor, val schedulersProvider: ISchedulersProvider) : ViewModel() {

    private var disposable: Disposable? = null
    var trackingPoints: MutableLiveData<List<List<LatLng>>> = MutableLiveData()

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

        disposable = singleObservable
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe({
                trackingPoints.value = it.trackingPoints
            }, {})
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}