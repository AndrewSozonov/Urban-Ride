package com.andrewsozonov.urbanride.di.app

import com.andrewsozonov.urbanride.di.fragment.FragmentComponent
import com.andrewsozonov.urbanride.di.service.ServiceComponent
import dagger.Component
import javax.inject.Singleton

/**
 * Главный компонент для получения зависимостей
 *
 * @author Андрей Созонов
 * */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun fragmentComponent(): FragmentComponent

    fun serviceComponent(): ServiceComponent
}