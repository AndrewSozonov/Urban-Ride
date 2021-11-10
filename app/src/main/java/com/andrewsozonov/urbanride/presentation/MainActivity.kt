package com.andrewsozonov.urbanride.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.databinding.ActivityMainBinding
import com.andrewsozonov.urbanride.util.Constants.ACTION_SHOW_RIDING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Главное активити приложения
 * Показывает 3 фрагмента через Bottom Navigation View
 * Проверяет настройки и устанавливает светлую/темную тему.
 *
 * @author Андрей Созонов
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setColorTheme()

        setContentView(binding.root)
        navigateToRideFragment(intent)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ride, R.id.navigation_history, R.id.navigation_settings
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setColorTheme() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkThemeOn =
            sharedPrefs.getBoolean(getString(R.string.dark_theme_pref_key), false)
        if (isDarkThemeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToRideFragment(intent)
    }

    private fun navigateToRideFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_RIDING_FRAGMENT) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.action_ridingFragment)
        }
    }
}

