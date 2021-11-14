package com.andrewsozonov.urbanride.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
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
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.presentation.MainActivity
import com.andrewsozonov.urbanride.presentation.service.model.LocationPoint
import com.andrewsozonov.urbanride.util.Constants.ACTION_SHOW_RIDING_FRAGMENT
import com.andrewsozonov.urbanride.util.Constants.LOCATION_UPDATE_INTERVAL
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_CHANNEL_ID
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.andrewsozonov.urbanride.util.Constants.NOTIFICATION_ID
import com.andrewsozonov.urbanride.util.Constants.PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_PAUSED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STARTED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STOPPED
import com.andrewsozonov.urbanride.util.Constants.START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.TIMER_DELAY
import com.andrewsozonov.urbanride.util.DataFormatter
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Сервис получает геолокацию устройства
 * Стартует таймер во время работы
 * Обновляет данные в [MutableLiveData]
 *
 * @author Андрей Созонов
 */
class LocationService : LifecycleService() {

    @Inject
    lateinit var viewModelFactory: LocationViewModelFactory
    private lateinit var locationViewModel: LocationServiceViewModel


    private var isServiceResumed = false
    private var serviceStopped = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isTimerEnabled = false
    private var intervalTime = 0L
    private var totalTime = 0L
    private var timeStarted = 0L

    private val isTracking = MutableLiveData<Boolean>()

    private val trackingPoints = MutableLiveData<MutableList<MutableList<LocationPoint>>>()
    private val rideTime = MutableLiveData<Long>()
//    private val currentSpeed = MutableLiveData<Float>()

    override fun onCreate() {
        super.onCreate()
        createViewModel()

        trackingPoints.observe(this, {
            locationViewModel.updateTrackingPoints(it)
        })

        rideTime.observe(this, {
            locationViewModel.updateTimerValue(it)
        })

        isTracking.observe(this, {
            updateLocationTracking(it)
        })

        initLocationLiveData()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
    }

    private fun initLocationLiveData() {
        isTracking.postValue(false)
        trackingPoints.value?.clear()
        trackingPoints.postValue(mutableListOf())
        rideTime.postValue(0L)
    }

    private fun createViewModel() {
        App.getAppComponent()?.activityComponent()?.inject(this)
        locationViewModel = viewModelFactory.create(LocationServiceViewModel::class.java)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(SERVICE_STATUS_STARTED)
                    if (!isServiceResumed) {
                        initLocationLiveData()
                        startForegroundService()
                        isServiceResumed = true
                    } else {
                        startTimer()
                    }
                }
                PAUSE_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(SERVICE_STATUS_PAUSED)
                    pauseService()
                }
                STOP_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(SERVICE_STATUS_STOPPED)
                    stopService()
                }
                else -> return super.onStartCommand(intent, flags, startId)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopService() {
        serviceStopped = true
        isServiceResumed = false
        pauseService()
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
                updateNotification(createNotification())
                delay(TIMER_DELAY)
            }
        }
        totalTime += intervalTime
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (PermissionsUtil.checkPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
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

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (isTracking.value == true) {
                p0.locations.let { locations ->
                    for (location in locations) {
                        addTrackingPoint(location)
//                        currentSpeed.value = location.speed / 1000 * 3600
                    }
                }
            }
        }
    }

    private fun addTrackingPoint(location: Location?) {
        location?.let {
            val position =
                LocationPoint(location.latitude, location.longitude, location.speed, rideTime.value!!, 0f)
            trackingPoints.value?.apply {
                Log.d("addTrackingPoint", "lat: ${position.latitude}  long: ${position.longitude}  speed: ${position.speed}  time: ${position.time}")
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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_ride_white_24dp)
            .setContentText(rideTime.value?.let { DataFormatter.formatTime(it) })
            .setContentIntent(getMainActivityIntent())
        return notificationBuilder.build()
    }

    private fun getMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.action = ACTION_SHOW_RIDING_FRAGMENT
        return PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification(notification: Notification) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
