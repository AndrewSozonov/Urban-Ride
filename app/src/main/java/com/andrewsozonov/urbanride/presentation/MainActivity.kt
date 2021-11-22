package com.andrewsozonov.urbanride.presentation

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.databinding.ActivityMainBinding
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_SHOW_RIDING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


/**
 * Главное активити приложения
 * Показывает 3 фрагмента через Bottom Navigation View
 * Проверяет настройки и устанавливает светлую/темную тему.
 * Проверяет доступ в интернет.
 * Проверяет включена ли геолокация.
 *
 * @author Андрей Созонов
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallBack: ConnectivityManager.NetworkCallback
    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerNetworkListener()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setColorTheme()
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_ride, R.id.navigation_history, R.id.navigation_settings
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navigateToRideFragment(intent)
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

    private fun registerNetworkListener() {
        val snackBar =
            Snackbar.make(
                binding.navView,
                getString(R.string.check_internet_connection),
                Snackbar.LENGTH_INDEFINITE
            )

        networkCallBack = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                snackBar.dismiss()
            }

            override fun onLost(network: Network) {
                if (!isOnline(connectivityManager)) {
                    snackBar.show()
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallBack)
        } else {
            val builder = NetworkRequest.Builder()
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            val networkRequest = builder.build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallBack)
        }
    }

    private fun isOnline(connMgr: ConnectivityManager): Boolean {
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun checkIsLocationOn() {

        val gpsEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled: Boolean =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.location_option_required))
                .setPositiveButton(
                    getString(R.string.open_settings)
                ) { dialog, which ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    override fun onResume() {
        checkIsLocationOn()
        super.onResume()
    }

    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallBack)
        super.onDestroy()
    }

}

