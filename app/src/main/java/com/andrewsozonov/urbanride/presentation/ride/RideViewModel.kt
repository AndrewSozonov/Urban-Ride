package com.andrewsozonov.urbanride.presentation.ride

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.presentation.model.LocationPoint
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * [ViewModel] прикреплена к [RideFragment]
 * Получает данные из фрагмента, вычисляет расстояние и скорость.
 * Сохраняет в базу данных.
 *
 * @param repository интерфейс репозитория
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class RideViewModel(val repository: BaseRepository, val schedulersProvider: ISchedulersProvider) :
    ViewModel() {

    private var disposable: Disposable? = null

    val serviceStatus: LiveData<String> = repository.getServiceStatus()

    /**
     * [MutableLiveData] хранит значение таймера для обновления во фрагменте
     */
    val timerLiveData: LiveData<String> = repository.getTimerValue()


    /**
     * [MutableLiveData] хранит модель данных: скорость, расстояние и среднюю скорость для отображения во фрагменте
     */
    val data: MutableLiveData<RideDataModel> = repository.getTrackingData()

    /**
     * Собирает модель данных [Ride] для БД
     *
     * @param mapImage изображение карты с конечным маршрутом
     */
    fun saveRide(mapImage: Bitmap) {
        val observable = Completable.fromCallable {
            repository.addRide(mapImage)
        }

        disposable = observable
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}