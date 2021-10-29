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
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.databinding.FragmentRideBinding
import com.andrewsozonov.urbanride.model.RideDataModel
import com.andrewsozonov.urbanride.service.LocationService
import com.andrewsozonov.urbanride.util.Constants.PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject


class RideFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var rideViewModel: RideViewModel
    private var _binding: FragmentRideBinding? = null
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView

    private var buttonStart: FloatingActionButton? = null
    private var buttonStop: FloatingActionButton? = null
    private var distanceTextView: TextView? = null
    private var speedTextView: TextView? = null
    private var averageSpeedTextView: TextView? = null
    private var timer: TextView? = null

    var distance: Float = 0.0f
    private var serviceStatus: String = "Stopped"

    private var trackingPoints = mutableListOf<MutableList<LatLng>>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    // This property is only valid between onCreateView and
    // onDestroyView.
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

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)

            subscribeToObservers()
            drawRoute()

        }

        buttonStart?.setOnClickListener {
            if (PermissionsUtil.checkPermissions(requireContext())) {
                when (serviceStatus) {
                    "Started" -> operateService(PAUSE_LOCATION_SERVICE)
                    "Paused" -> operateService(START_LOCATION_SERVICE)
                    else -> {
                        clearMap()
                        operateService(START_LOCATION_SERVICE)
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "This application require Location permission to work properly",
                    Toast.LENGTH_SHORT
                ).show()
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
            updateRoute()
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
            "Started" -> {
                buttonStart?.setImageLevel(1)
                buttonStop?.visibility = GONE
            }
            "Paused" -> {
                buttonStart?.setImageLevel(0)
                buttonStop?.visibility = VISIBLE
            }
            "Stopped" -> {
                buttonStart?.setImageLevel(0)
                buttonStop?.visibility = GONE
            }
        }
    }

    private fun updateTimer(time: String) {
        timer?.text = time
    }

    private fun updateData(model: RideDataModel) {
        speedTextView?.text = resources.getString(R.string.km_h, model.speed)
        distanceTextView?.text = resources.getString(R.string.km, model.distance)
        averageSpeedTextView?.text = resources.getString(R.string.km_h, model.averageSpeed)
    }

    private fun drawRoute() {
        if (trackingPoints.isNotEmpty()) {
            clearMap()
        }
        for (line in trackingPoints) {
            val polylineOptions = PolylineOptions()
                .width(8f)
                .color(R.color.middle_cyan)
                .jointType(JointType.ROUND)
                .addAll(line)
            map.addPolyline(polylineOptions)
        }
    }

    private fun updateRoute() {
        if (trackingPoints.isNotEmpty() && trackingPoints.last().size > 1) {
            val lastLatLng = trackingPoints.last()[trackingPoints.last().size - 2]
            val currentLatLng = trackingPoints.last().last()
            val polylineOptions = PolylineOptions()
                .width(8f)
                .color(R.color.middle_cyan)
                .jointType(JointType.ROUND)
                .add(lastLatLng, currentLatLng)
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
        val bounds = LatLngBounds.Builder()
        for (line in trackingPoints) {
            for (point in line) {
                bounds.include(point)
            }
        }

        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
        map.setOnMapLoadedCallback {
            saveRide()
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
        mapView.onStart()
        Log.d("onStart", "tracking points: $trackingPoints")

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        drawRoute()
        Log.d("onResume", "tracking points: $trackingPoints")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        Log.d("onPause", "tracking points: $trackingPoints")
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        Log.d("onStop", "tracking points: $trackingPoints")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestLocationPermissions() {

        if (!PermissionsUtil.checkPermissions(requireContext())) {

            val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    permissions.entries.forEach {
                        Log.e("DEBUG", "${it.key} = ${it.value}")
                    }

                    val granted = permissions.entries.all {
                        it.value == true
                    }

                    if (granted) {
//                subscribeToObservers()
//                drawRoute()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "This application require Location permission to work properly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                requestMultiplePermissions.launch(
                    arrayOf(
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
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



