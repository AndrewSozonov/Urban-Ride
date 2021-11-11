package com.andrewsozonov.urbanride.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrewsozonov.urbanride.repository.BaseRepository
import com.andrewsozonov.urbanride.util.Converter
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.disposables.Disposable

class MapViewModel(val repository: BaseRepository, val schedulersProvider: ISchedulersProvider) : ViewModel() {

    private var disposable: Disposable? = null
    var trackingPoints: MutableLiveData<List<List<LatLng>>> = MutableLiveData()

    fun getRide(id: Int) {
        val singleObservable = Single.fromCallable {
            repository.getRideById(id)
        }

        disposable = singleObservable
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
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