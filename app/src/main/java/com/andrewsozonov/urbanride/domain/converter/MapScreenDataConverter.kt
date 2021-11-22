package com.andrewsozonov.urbanride.domain.converter

import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.constants.UnitsContants.METERS_IN_KM
import com.google.android.gms.maps.model.LatLng

/**
 * Конвертирует из модели базы данных в модель для отображения на экране Map
 *
 * @author Андрей Созонов
 */
class MapScreenDataConverter {

    /**
     * Конвертирует из модели [RideDBModel] в модель [RideModel]
     * @param rideDBModel модель данных из БД
     * @return модель данных [RideModel]
     */
    fun convertFromRideDBModelToRideModel(rideDBModel: RideDBModel): RideModel {
        return RideModel(
            rideDBModel.distance / METERS_IN_KM,
            rideDBModel.trackingPoints.last().last().speed,
            rideDBModel.averageSpeed,
            convertLocationPointToLatLng(rideDBModel.trackingPoints),
            true
        )
    }

    private fun convertLocationPointToLatLng(points: List<List<LocationPoint>>): List<List<LatLng>> {
        val trackingPoints: MutableList<MutableList<LatLng>> = mutableListOf()
        for (list in points) {
            val listLatLng: MutableList<LatLng> = mutableListOf()
            for (point in list) {
                val latLng = LatLng(point.latitude, point.longitude)
                listLatLng.add(latLng)
            }
            trackingPoints.add(listLatLng)
        }
        return trackingPoints
    }
}