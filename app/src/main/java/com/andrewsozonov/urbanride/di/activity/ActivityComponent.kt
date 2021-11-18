package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.presentation.service.LocationService
import com.andrewsozonov.urbanride.presentation.history.HistoryFragment
import com.andrewsozonov.urbanride.presentation.map.MapFragment
import com.andrewsozonov.urbanride.presentation.ride.RideFragment
import dagger.Subcomponent


/**
 * Компонент для внедрения зависимостей во фрагменты
 *
 * @author Андрей Созонов
 */

@Subcomponent(
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(rideFragment: RideFragment)

    fun inject(historyFragment: HistoryFragment)

    fun inject(locationService: LocationService)

    fun inject(mapFragment: MapFragment)
}