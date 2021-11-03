package com.andrewsozonov.urbanride.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.repository.MainRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * [ViewModel] прикреплена к [HistoryFragment]
 * Загружает список поездок из БД для отображения во фрагменте
 *
 * @author Андрей Созонов
 */
class HistoryViewModel : ViewModel() {


    @Inject
    lateinit var repository: MainRepository
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * [MutableLiveData] хранит модель данных для отображения во фрагменте
     */
    val listOfRides = MutableLiveData<List<Ride>>()


    init {
        App.getAppComponent()?.inject(this)
    }

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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getRidesFromDB()
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listOfRides.value = it
                Log.d("getRidesFromDB", " listOfRides $it")
            }, {})
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}