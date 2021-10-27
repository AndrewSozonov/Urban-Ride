package com.andrewsozonov.urbanride

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.andrewsozonov.urbanride.databinding.ActivityMainBinding
import com.andrewsozonov.urbanride.util.Constants.ACTION_SHOW_RIDING_FRAGMENT

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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

