package com.andrewsozonov.urbanride.data.repository

import android.content.SharedPreferences
import com.andrewsozonov.urbanride.util.Constants.UNITS_KEY
import com.andrewsozonov.urbanride.util.Constants.UNITS_KILOMETERS

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    override fun getUnits(): Boolean {
        return sharedPreferences.getString(
            UNITS_KEY,
            UNITS_KILOMETERS
        ) == UNITS_KILOMETERS
    }
}