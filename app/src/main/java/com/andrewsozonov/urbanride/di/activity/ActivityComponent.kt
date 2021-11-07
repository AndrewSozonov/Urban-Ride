package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.ui.history.HistoryFragment
import com.andrewsozonov.urbanride.ui.ride.RideFragment
import dagger.Subcomponent


/**
 * Компонент для внедрения зависимостей в [MainActivity]
 *
 * @author Андрей Созонов
 */

@Subcomponent(
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(rideFragment: RideFragment)

    fun inject(historyFragment: HistoryFragment)
}