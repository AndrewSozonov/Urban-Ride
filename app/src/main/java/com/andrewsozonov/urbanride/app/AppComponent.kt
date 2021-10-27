package com.andrewsozonov.urbanride.app

import com.andrewsozonov.urbanride.ui.history.HistoryViewModel
import com.andrewsozonov.urbanride.ui.ride.RideFragment
import com.andrewsozonov.urbanride.ui.ride.RideViewModel
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
//@Module(includes = [AppModule::class])
interface AppComponent {

    fun inject(viewModel: RideViewModel)

    fun inject(viewModel: HistoryViewModel)
}