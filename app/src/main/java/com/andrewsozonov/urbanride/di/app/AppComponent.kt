package com.andrewsozonov.urbanride.di.app

import com.andrewsozonov.urbanride.di.activity.ActivityComponent
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

    fun activityComponent(): ActivityComponent
}