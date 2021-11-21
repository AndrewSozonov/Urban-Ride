package com.andrewsozonov.urbanride.presentation.ride

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.presentation.BaseViewModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import io.reactivex.Completable

/**
 * [ViewModel] прикреплена к [RideFragment]
 * Получает данные из фрагмента, вычисляет расстояние и скорость.
 * Сохраняет в базу данных.
 *
 * @param interactor интерактор экрана Ride
 * @param schedulersProvider интерфейс предоставляющий потоки
 *
 * @author Андрей Созонов
 */
class RideViewModel(
    private val interactor: RideInteractor,
    val schedulersProvider: ISchedulersProvider
) :
    BaseViewModel() {

    val serviceStatus: LiveData<String> = interactor.getServiceStatus()

    /**
     * [MutableLiveData] хранит значение таймера для обновления во фрагменте
     */
    val timerLiveData: LiveData<String> = interactor.getTimerValue()

    /**
     * [MutableLiveData] хранит модель данных: скорость, расстояние, среднюю скорость и координаты для отображения во фрагменте
     */
    val data: LiveData<RideModel> = interactor.getTrackingData()

    /**
     * Сохраняет поездку в БД
     * @param mapImage изображение карты с конечным маршрутом
     */
    fun saveRide(mapImage: Bitmap) {
        val observable = Completable.fromCallable {
            interactor.addRide(mapImage)
        }
        compositeDisposable.add(
            observable
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe()
        )
    }
}