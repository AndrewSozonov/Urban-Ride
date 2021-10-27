package com.andrewsozonov.urbanride.app

import android.app.Application


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

        fun setComponent(component: AppComponent?) {
            appComponent = component
        }
    }

    private fun generateAppComponent(): AppComponent {
        return DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}