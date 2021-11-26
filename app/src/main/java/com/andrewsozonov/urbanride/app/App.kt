package com.andrewsozonov.urbanride.app

import android.app.Application
import com.andrewsozonov.urbanride.di.app.AppComponent
import com.andrewsozonov.urbanride.di.app.AppModule
import com.andrewsozonov.urbanride.di.app.DaggerAppComponent

/**
 * Класс application приложения
 *
 * @author Андрей Созонов
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = generateAppComponent()
    }

    companion object {
        private var appComponent: AppComponent? = null

        fun getAppComponent(): AppComponent? {
            return appComponent
        }
    }

    private fun generateAppComponent(): AppComponent {
        return DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}