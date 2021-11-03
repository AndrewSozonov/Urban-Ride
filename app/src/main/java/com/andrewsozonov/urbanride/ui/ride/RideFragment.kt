package com.andrewsozonov.urbanride.ui.ride

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.databinding.FragmentRideBinding
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.service.LocationService
import com.andrewsozonov.urbanride.util.Constants.PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.POLYLINE_WIDTH
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_PAUSED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STARTED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STOPPED
import com.andrewsozonov.urbanride.util.Constants.START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Converter
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

/**
 * Главный фрагмент приложения.
 * Показывает карту.
 * При нажатии на кнопку запуска, запускает [LocationService] и получает координаты пользователя.
 * Обновляет карту на основе полученных координат и рисует маршрут.
 * Координаты и время отправляет во [RideViewModel] для подсчета расстояния и скорости
 * Полученные из [RideViewModel] данные отображает в соответсвующих полях.
 * При нажатии на кнопку стоп отправлет данные в базу.
 */
class RideFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var rideViewModel: RideViewModel
    private var _binding: FragmentRideBinding? = null
    private lateinit var map: GoogleMap

    private var mapView: MapView? = null

    private var buttonStart: FloatingActionButton? = null
    private var buttonStop: FloatingActionButton? = null
    private var distanceTextView: TextView? = null
    private var speedTextView: TextView? = null
    private var averageSpeedTextView: TextView? = null
    private var timer: TextView? = null

    private var serviceStatus: String = SERVICE_STATUS_STOPPED
    private var trackingPoints = mutableListOf<MutableList<LatLng>>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rideViewModel =
            ViewModelProvider(this).get(RideViewModel::class.java)

        _binding = FragmentRideBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        buttonStart = binding.startRideButton
        buttonStop = binding.stopRideButton
        timer = binding.durationTextView
        distanceTextView = binding.distanceTextView
        speedTextView = binding.speedTextView
        averageSpeedTextView = binding.averageSpeedTextView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            map = it
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)

            subscribeToObservers()
            drawRoute()
        }

        buttonStart?.setOnClickListener {
            if (PermissionsUtil.checkPermissions(requireContext())) {
                when (serviceStatus) {
                    SERVICE_STATUS_STARTED -> operateService(PAUSE_LOCATION_SERVICE)
                    SERVICE_STATUS_PAUSED -> operateService(START_LOCATION_SERVICE)
                    else -> {
                        clearMap()
                        operateService(START_LOCATION_SERVICE)
                    }
                }
            } else {
                showPermissionToast()
            }
        }
        buttonStop?.setOnClickListener {
            zoomMapToSaveForDB()
        }
    }

    private fun subscribeToObservers() {
        LocationService.serviceStatus.observe(viewLifecycleOwner, {
            updateUi(it)
        })

        LocationService.rideTime.observe(viewLifecycleOwner, {
            rideViewModel.calculateTime(it)
        })

        LocationService.trackingPoints.observe(viewLifecycleOwner, {
            trackingPoints = it
//            updateRoute()
            drawRoute()
            rideViewModel.calculateData(trackingPoints)
        })

        rideViewModel.timerLiveData.observe(viewLifecycleOwner, {
            updateTimer(it)
        })

        rideViewModel.data.observe(viewLifecycleOwner, {
            updateData(it)
        })
    }

    private fun operateService(action: String) {
        val intent = Intent(requireContext(), LocationService::class.java)
        intent.action = action
        requireContext().startService(intent)
    }

    private fun stopRide() {
        operateService(STOP_LOCATION_SERVICE)
        buttonStop?.visibility = GONE
    }

    override fun onMyLocationButtonClick(): Boolean {
        return true
    }

    override fun onMyLocationClick(p0: Location) {
    }

    private fun updateUi(serviceStatus: String) {
        this.serviceStatus = serviceStatus
        when (serviceStatus) {
            SERVICE_STATUS_STARTED -> {
                buttonStart?.setImageLevel(1)
                buttonStop?.visibility = GONE
            }
            SERVICE_STATUS_PAUSED -> {
                buttonStart?.setImageLevel(0)
                buttonStop?.visibility = VISIBLE
            }
            SERVICE_STATUS_STOPPED -> {
                buttonStart?.setImageLevel(0)
                buttonStop?.visibility = GONE
            }
        }
    }

    private fun updateTimer(time: String) {
        timer?.text = time
    }

    private fun updateData(model: RideDataModel) {

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val metricSystemChose =
            sharedPrefs.getString(
                getString(R.string.unit_system_pref_key),
                getString(R.string.units_kilometers)
            )

        if (metricSystemChose == getString(R.string.units_kilometers)) {
            speedTextView?.text = resources.getString(R.string.km_h, model.speed.toString())
            distanceTextView?.text = resources.getString(R.string.km, model.distance.toString())
            averageSpeedTextView?.text =
                resources.getString(R.string.km_h, model.averageSpeed.toString())
        } else {
            speedTextView?.text = resources.getString(
                R.string.miles_h,
                Converter.convertKilometersToMiles(model.speed).toString()
            )
            distanceTextView?.text = resources.getString(
                R.string.miles,
                Converter.convertKilometersToMiles(model.distance).toString()
            )
            averageSpeedTextView?.text = resources.getString(
                R.string.miles_h,
                Converter.convertKilometersToMiles(model.averageSpeed).toString()
            )
        }
    }

    private fun drawRoute() {
        if (trackingPoints.isNotEmpty()) {
            clearMap()
        }
        for (line in trackingPoints) {
            if (trackingPoints.last() == line) {
                val polylineOptions = PolylineOptions()
                    .width(POLYLINE_WIDTH)
                    .color(resources.getColor(R.color.middle_blue))
                    .jointType(JointType.ROUND)
                    .addAll(line)
                    .endCap(
                        CustomCap(
                            BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 70f
                        )
                    )
                map.addPolyline(polylineOptions)
            } else {
                val polylineOptions = PolylineOptions()
                    .width(POLYLINE_WIDTH)
                    .color(resources.getColor(R.color.middle_blue))
                    .jointType(JointType.ROUND)
                    .addAll(line)
                map.addPolyline(polylineOptions)
            }
        }
        moveCameraToCurrentLocation()

    }

    private fun updateRoute() {
        if (trackingPoints.isNotEmpty() && trackingPoints.last().size > 1) {
            val lastLatLng = trackingPoints.last()[trackingPoints.last().size - 2]
            val currentLatLng = trackingPoints.last().last()
            val polylineOptions = PolylineOptions()
                .width(POLYLINE_WIDTH)
                .color(R.color.middle_cyan)
                .jointType(JointType.ROUND)
                .add(lastLatLng, currentLatLng)
                .endCap(
                    CustomCap(
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 6f
                    )
                )
            map.addPolyline(polylineOptions)
        }
        moveCameraToCurrentLocation()
    }

    private fun clearMap() {
        map.clear()
    }

    private fun moveCameraToCurrentLocation() {
        if (trackingPoints.isNotEmpty() && trackingPoints.last().size > 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(trackingPoints.last().last(), 16f))
        }
    }

    private fun zoomMapToSaveForDB() {
        Log.d("zoomMap", " trackingPoints: $trackingPoints")

        if (trackingPoints.any {
            it.isNotEmpty()
            }) {
            val bounds = LatLngBounds.Builder()
            for (line in trackingPoints) {
                for (point in line) {
                    bounds.include(point)
                }
            }

            map.moveCamera(
                mapView?.width?.let {
                    mapView?.height?.let { it1 ->
                        CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            it,
                            it1,
                            (mapView?.height!! * 0.05f).toInt()
                        )
                    }
                }
            )
            map.setOnMapLoadedCallback {
                saveRide()
            }
        }
    }

    private fun saveRide() {
        map.snapshot { bitmap ->
            rideViewModel.saveRide(bitmap)
            stopRide()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        Log.d("onStart", "mapView: $mapView")

    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        drawRoute()
        Log.d("onResume", "mapView: $mapView")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
        Log.d("onSaveInstanceState", "mapView: $mapView")

    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        Log.d("onPause", "mapView: $mapView")
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        Log.d("onStop", "mapView: $mapView")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPermissionToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.permission_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun requestLocationPermissions() {

        if (!PermissionsUtil.checkPermissions(requireContext())) {

            val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                    val granted = permissions.entries.all {
                        it.value == true
                    }
                    if (!granted) {
                        showPermissionToast()
                    }
                }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            } else {
                val requestPermissionLauncher =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
                        Log.e("DEBUG", "ACCESS_FINE_LOCATION = ${it}")
                    }
                val requestPermissionLauncher2 =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
                        Log.e("DEBUG", "ACCESS_BACKGROUND_LOCATION = ${it}")
                    }
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                requestPermissionLauncher2.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }
}



