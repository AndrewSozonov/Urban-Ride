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
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.models.presentation.service.ServiceStatus
import com.andrewsozonov.urbanride.presentation.activity.MainActivity
import com.andrewsozonov.urbanride.util.DataFormatter
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_SHOW_RIDING_FRAGMENT
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.LocationConstants.LOCATION_UPDATE_INTERVAL
import com.andrewsozonov.urbanride.util.constants.LocationConstants.NOTIFICATION_CHANNEL_ID
import com.andrewsozonov.urbanride.util.constants.LocationConstants.NOTIFICATION_CHANNEL_NAME
import com.andrewsozonov.urbanride.util.constants.LocationConstants.NOTIFICATION_ID
import com.andrewsozonov.urbanride.util.constants.LocationConstants.TIMER_DELAY
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
 * Foreground ???????????? ???????????????? ???????????????????? ????????????????????
 * ???????????????? ???????????? ???? ?????????? ????????????
 * ?????????????????? ???????????? ?? [LocationServiceViewModel]
 *
 * @author ???????????? ??????????????
 */
class LocationService : LifecycleService() {

    @Inject
    lateinit var viewModelFactory: LocationViewModelFactory
    private lateinit var locationViewModel: LocationServiceViewModel

    private var isServiceResumed = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var intervalTime = 0L
    private var totalTime = 0L
    private var timeStarted = 0L

    private val isTracking = MutableLiveData<Boolean>()
    private val trackingPoints: MutableList<MutableList<LocationPoint>> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        createViewModel()

        isTracking.observe(this, {
            updateLocationTracking(it)
        })

        initLocationLiveData()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
    }

    private fun initLocationLiveData() {
        isTracking.postValue(false)
        trackingPoints.clear()
        locationViewModel.updateTrackingPoints(trackingPoints)
        totalTime = 0L
        locationViewModel.updateTimerValue(totalTime)
    }

    private fun createViewModel() {
        App.getAppComponent()?.serviceComponent()?.inject(this)
        locationViewModel = viewModelFactory.create(LocationServiceViewModel::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(ServiceStatus.STARTED)
                    if (!isServiceResumed) {
                        initLocationLiveData()
                        startForegroundService()
                        isServiceResumed = true
                    } else {
                        startTimer()
                    }
                }
                ACTION_PAUSE_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(ServiceStatus.PAUSED)
                    pauseService()
                }
                ACTION_STOP_LOCATION_SERVICE -> {
                    locationViewModel.updateServiceStatus(ServiceStatus.STOPPED)
                    stopService()
                }
                else -> return super.onStartCommand(intent, flags, startId)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopService() {
        isServiceResumed = false
        pauseService()
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    private fun startTimer() {
        addEmptyPath()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value == true) {
                intervalTime = System.currentTimeMillis() - timeStarted
                locationViewModel.updateTimerValue(totalTime + intervalTime)
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
                    priority = PRIORITY_HIGH_ACCURACY
                    isWaitForAccurateLocation = true
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
                    if (!checkIfLocationsEqual(locations.last())) {
//                        addTrackingPoint(locations.last())
                        locations.map {
                            if (!checkIfLocationsEqual(it)) {
                                addTrackingPoint(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkIfLocationsEqual(location: Location): Boolean {
        if (trackingPoints.size >= 2) {
            val lastLatitude = trackingPoints[trackingPoints.size - 2].last().latitude
            val lastLongitude = trackingPoints[trackingPoints.size - 2].last().longitude
            return (location.latitude == lastLatitude && location.longitude == lastLongitude)
        }
        return false
    }

    private fun addTrackingPoint(location: Location?) {
        location?.let {
            val position =
                LocationPoint(
                    location.latitude,
                    location.longitude,
                    location.speed,
                    totalTime + intervalTime,
                    0f
                )
            trackingPoints.apply {
                last().add(position)
                val distance = calculateDistance(this)
                this.last().last().distance = distance
                locationViewModel.updateTrackingPoints(trackingPoints)
                Log.d(
                    "LocationService",
                    "lat: ${position.latitude}  long: ${position.longitude}  speed: ${position.speed}  time: ${position.time}  distance: ${position.distance}"
                )
            }
        }
    }

    private fun calculateDistance(trackingPoints: List<List<LocationPoint>>): Float {
        var distance = 0.0
        for (path in trackingPoints) {
            for (i in 0..path.size - 2) {
                val point1 = path[i]
                val point2 = path[i + 1]
                val result = FloatArray(1)
                Location.distanceBetween(
                    point1.latitude,
                    point1.longitude,
                    point2.latitude,
                    point2.longitude,
                    result
                )
                distance += result[0]
            }
        }
        return distance.toInt().toFloat()
    }

    private fun addEmptyPath() = trackingPoints.apply {
        add(mutableListOf())
        locationViewModel.updateTrackingPoints(this)
    }

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
            .setContentText((totalTime + intervalTime).let { DataFormatter.formatTime(it) })
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
