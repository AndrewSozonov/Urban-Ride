package com.andrewsozonov.urbanride.di.service

import com.andrewsozonov.urbanride.presentation.service.LocationService
import dagger.Subcomponent

/**
 * Компонент для внедрения зависимостей в сервис
 *
 * @author Андрей Созонов
 */
@Subcomponent(
    modules = [ServiceModule::class]
)
interface ServiceComponent {

    fun inject(locationService: LocationService)
}