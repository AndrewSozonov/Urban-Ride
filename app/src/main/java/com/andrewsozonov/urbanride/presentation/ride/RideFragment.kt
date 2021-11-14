package com.andrewsozonov.urbanride.presentation.ride

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.databinding.FragmentRideBinding
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.presentation.service.LocationService
import com.andrewsozonov.urbanride.util.BitmapHelper
import com.andrewsozonov.urbanride.util.Constants.PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.POLYLINE_WIDTH
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_PAUSED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STARTED
import com.andrewsozonov.urbanride.util.Constants.SERVICE_STATUS_STOPPED
import com.andrewsozonov.urbanride.util.Constants.START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.Constants.STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
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

    private var trackingStatus: String = SERVICE_STATUS_STOPPED
    private var trackingPoints = listOf<List<LatLng>>()
    private var isUnitsMetric = true

    @Inject
    lateinit var viewModelFactory: RideViewModelFactory
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        createViewModel()

        _binding = FragmentRideBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = binding.mapView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            map = it
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)

            checkPreferences()
            subscribeToObservers()

            if (trackingStatus == SERVICE_STATUS_STOPPED && trackingPoints.isNotEmpty()) {
                drawFinalRoute()
            } else {
                drawRoute()
            }

        }

        binding.startRideButton.setOnClickListener {
            if (PermissionsUtil.checkPermissions(requireContext())) {
                when (trackingStatus) {
                    SERVICE_STATUS_STARTED -> {
                        trackingStatus = SERVICE_STATUS_PAUSED
                        operateService(PAUSE_LOCATION_SERVICE)
                    }

                    SERVICE_STATUS_PAUSED -> {
                        trackingStatus = SERVICE_STATUS_STARTED
                        operateService(START_LOCATION_SERVICE)
                    }
                    else -> {
                        trackingStatus = SERVICE_STATUS_STARTED
                        clearMap()
                        operateService(START_LOCATION_SERVICE)
                    }
                }
                updateUi()
            } else {
                showPermissionToast()
            }
        }
        binding.stopRideButton.setOnClickListener {
            trackingStatus = SERVICE_STATUS_STOPPED
            updateUi()
            drawFinalRoute()
            zoomMapToSaveForDB()
        }
    }

    private fun createViewModel() {
        App.getAppComponent()?.activityComponent()?.inject(this)
        rideViewModel = viewModelFactory.create(RideViewModel::class.java)

    }

    private fun subscribeToObservers() {

        rideViewModel.serviceStatus.observe(viewLifecycleOwner, {
            trackingStatus = it
            updateUi()
        })
        rideViewModel.timerLiveData.observe(viewLifecycleOwner, {
            updateTimer(it)
        })

        rideViewModel.data.observe(viewLifecycleOwner, {
            trackingPoints = it.trackingPoints
            drawRoute()
            updateData(it)
        })
    }

    private fun checkPreferences() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        isUnitsMetric = sharedPrefs.getString(
            getString(R.string.unit_system_pref_key),
            getString(R.string.units_kilometers)
        ) == getString(R.string.units_kilometers)
    }

    private fun operateService(action: String) {
        val intent = Intent(requireContext(), LocationService::class.java)
        intent.action = action
        requireContext().startService(intent)
    }

    private fun stopRide() {
        operateService(STOP_LOCATION_SERVICE)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return true
    }

    override fun onMyLocationClick(p0: Location) {
    }

    private fun updateUi() {
        when (trackingStatus) {
            SERVICE_STATUS_STARTED -> {
                binding.startRideButton.setImageLevel(1)
                binding.stopRideButton.visibility = GONE
            }
            SERVICE_STATUS_PAUSED -> {
                binding.startRideButton.setImageLevel(0)
                binding.stopRideButton.visibility = VISIBLE
            }
            SERVICE_STATUS_STOPPED -> {
                binding.startRideButton.setImageLevel(0)
                binding.stopRideButton.visibility = GONE
            }
        }
    }

    private fun updateTimer(time: String) {
        binding.durationTextView.text = time
    }

    private fun updateData(model: RideModel) {

        if (isUnitsMetric) {
            binding.speedTextView.text = resources.getString(R.string.km_h, model.speed.toString())
            binding.distanceTextView.text = resources.getString(R.string.km, model.distance.toString())
            binding.averageSpeedTextView.text = resources.getString(R.string.km_h, model.averageSpeed.toString())
        } else {
            binding.speedTextView.text = resources.getString(R.string.miles_h, (model.speed).toString())
            binding.distanceTextView.text = resources.getString(R.string.miles, model.distance.toString())
            binding.averageSpeedTextView.text = resources.getString(R.string.miles_h, model.averageSpeed.toString()
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
                    .color(ContextCompat.getColor(requireContext(), R.color.middle_blue ))
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
                    .color(ContextCompat.getColor(requireContext(), R.color.middle_blue ))
                    .jointType(JointType.ROUND)
                    .addAll(line)
                map.addPolyline(polylineOptions)
            }
        }
        moveCameraToCurrentLocation()

    }

    private fun drawFinalRoute() {
        if (trackingPoints.isNotEmpty()) {
            clearMap()
        }
        for (line in trackingPoints) {
            val polylineOptions = PolylineOptions()
                .width(POLYLINE_WIDTH)
                .color(ContextCompat.getColor(requireContext(), R.color.middle_blue ))
                .jointType(JointType.ROUND)
                .addAll(line)
            map.addPolyline(polylineOptions)

        }
        addMarkers(map)
        moveCameraToCurrentLocation()
    }

    private fun addMarkers(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .title(getString(R.string.start))
                .position(trackingPoints.first().first())
                .icon(
                    BitmapHelper.vectorToBitmap(
                        requireContext(),
                        R.drawable.ic_start_flag,
                        ContextCompat.getColor(requireContext(), R.color.dark_blue )
                    )
                )
        )
        googleMap.addMarker(
            MarkerOptions()
                .title(getString(R.string.finish))
                .position(trackingPoints.last().last())
                .icon(
                    BitmapHelper.vectorToBitmap(
                        requireContext(),
                        R.drawable.ic_finish_flag,
                        ContextCompat.getColor(requireContext(), R.color.dark_blue )
                    )
                )
        )
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
                            (mapView?.height!! * 0.1f).toInt()
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
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        rideViewModel.setUnits(isUnitsMetric)
        drawRoute()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
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
                        Log.e("DEBUG", "ACCESS_FINE_LOCATION = $it")
                    }
                val requestPermissionLauncher2 =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
                        Log.e("DEBUG", "ACCESS_BACKGROUND_LOCATION = $it")
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



