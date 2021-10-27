package com.andrewsozonov.urbanride.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.andrewsozonov.urbanride.MainActivity
import com.google.android.gms.location.LocationCallback
import com.andrewsozonov.urbanride.util.Constants.ACTION_SHOW_RIDING_FRAGMENT
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_CHANNEL_ID
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_ID
import com.andrewsozonov.urbanride.util.Constants.PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LocationService : LifecycleService() {


    var isServiceResumed = false
    var serviceStopped = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isTimerEnabled = false
    private var intervalTime = 0L
    private var totalTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val trackingPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
        val rideTime = MutableLiveData<Long>()
    }

    private fun initLocationLiveData() {
        isTracking.postValue(false)
        trackingPoints.postValue(mutableListOf())
        rideTime.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        initLocationLiveData()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_LOCATION_SERVICE -> {
                    if (!isServiceResumed) {
                        Log.d("onStartCommand", "SERVICE_STARTED")
                        startForegroundService()
                        isServiceResumed = true

                    } else {
                        Log.d("onStartCommand", "SERVICE_RESUMED")
//                        startForegroundService()
                        startTimer()

                    }
                }
                PAUSE_LOCATION_SERVICE -> {
                    Log.d("onStartCommand", "PAUSE_LOCATION_SERVICE")
                    pauseService()
                }
                STOP_LOCATION_SERVICE -> {
                    Log.d("onStartCommand", "STOP_LOCATION_SERVICE")
                    stopService()
                } else -> return super.onStartCommand(intent, flags, startId)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopService() {
        serviceStopped = true
        isServiceResumed = false
        pauseService()
        initLocationLiveData()
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun startTimer() {
        addEmptyPath()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value == true) {
                intervalTime = System.currentTimeMillis() - timeStarted
                rideTime.postValue(totalTime + intervalTime)
                delay(1000)
            }
        }
       totalTime += intervalTime
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if (PermissionsUtil.checkPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = 5000L
                    fastestInterval = 2000L
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        }
    }

    val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (isTracking.value!!) {
                p0.locations.let { locations ->
                    for(location in locations) {
                        addTrackingpoint(location)
                        Log.d("locationCallback", "latitude: ${location.latitude}  longitude: ${location.longitude}")
                    }
                }
            }
        }

    }

    private fun addTrackingpoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            trackingPoints.value?.apply {
                last().add(position)
                trackingPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPath() = trackingPoints.value?.apply {
        add(mutableListOf())
        trackingPoints.postValue(this)
    } ?: trackingPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("Urban Ride")
            .setContentIntent(getMainActivityIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = ACTION_SHOW_RIDING_FRAGMENT
        return PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(channel)
    }


}
