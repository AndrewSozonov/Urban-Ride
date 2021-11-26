package com.andrewsozonov.urbanride.di.activity

import com.andrewsozonov.urbanride.presentation.activity.MainActivity
import dagger.Subcomponent

/**
 * Компонент для внедрения зависимостей в activity
 *
 * @author Андрей Созонов
 */
@Subcomponent(
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)
}