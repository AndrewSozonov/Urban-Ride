package com.andrewsozonov.urbanride.di.fragment

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
    modules = [FragmentModule::class]
)
interface FragmentComponent {

    fun inject(rideFragment: RideFragment)

    fun inject(historyFragment: HistoryFragment)

    fun inject(mapFragment: MapFragment)
}