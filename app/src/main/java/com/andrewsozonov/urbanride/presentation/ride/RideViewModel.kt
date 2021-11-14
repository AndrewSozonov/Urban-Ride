package com.andrewsozonov.urbanride.presentation.ride

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.domain.interactor.RideInteractor
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

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
class RideViewModel(private val interactor: RideInteractor, val schedulersProvider: ISchedulersProvider) :
    ViewModel() {

    private var disposable: Disposable? = null

    val serviceStatus: LiveData<String> = interactor.getServiceStatus()

    /**
     * [MutableLiveData] хранит значение таймера для обновления во фрагменте
     */
    val timerLiveData: LiveData<String> = interactor.getTimerValue()

    /**
     * Устанавливает значение единиц измерения мили или км
     * полученное из preferences
     *
     * @param isUnitsMetric если true - км, false - мили
     */
    fun setUnits(isUnitsMetric: Boolean) {
        interactor.setUnits(isUnitsMetric)
    }

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