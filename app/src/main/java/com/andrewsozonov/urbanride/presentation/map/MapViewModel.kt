package com.andrewsozonov.urbanride.presentation.map

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.Converter
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapViewModel(val repository: BaseRepository, val schedulersProvider: ISchedulersProvider) : ViewModel() {

    private var disposable: Disposable? = null
    var trackingPoints: MutableLiveData<List<List<LatLng>>> = MutableLiveData()

    fun getRide(id: Int) {
        val singleObservable = Single.fromCallable {
            repository.getRideById(id)
        }

        disposable = singleObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Converter.convertListLocationPointToListLatLng(it.trackingPoints) }
            .subscribe({
                trackingPoints.value = it
            }, {})
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}