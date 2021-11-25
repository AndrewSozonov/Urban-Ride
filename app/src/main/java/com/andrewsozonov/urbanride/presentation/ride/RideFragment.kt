package com.andrewsozonov.urbanride.presentation.ride

import android.Manifest
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.databinding.FragmentRideBinding
import com.andrewsozonov.urbanride.models.presentation.ride.RideModel
import com.andrewsozonov.urbanride.models.presentation.service.ServiceStatus
import com.andrewsozonov.urbanride.presentation.service.LocationService
import com.andrewsozonov.urbanride.util.BitmapHelper
import com.andrewsozonov.urbanride.util.PermissionsUtil
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_PAUSE_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_START_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.LocationConstants.ACTION_STOP_LOCATION_SERVICE
import com.andrewsozonov.urbanride.util.constants.MapConstants.CAMERA_ZOOM_SCALING_AFTER_STOP
import com.andrewsozonov.urbanride.util.constants.MapConstants.CAMERA_ZOOM_VALUE
import com.andrewsozonov.urbanride.util.constants.MapConstants.POLYLINE_WIDTH
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
 * Полученные из [RideViewModel] данные отображает в соответствующих полях.
 * При нажатии на кнопку стоп отправлет данные в БД.
 */
class RideFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var rideViewModel: RideViewModel
    private var _binding: FragmentRideBinding? = null
    private lateinit var map: GoogleMap
    private var mapView: MapView? = null

    private var trackingStatus: ServiceStatus = ServiceStatus.STOPPED
    private var trackingPoints = listOf<List<LatLng>>()

    @Inject
    lateinit var viewModelFactory: RideViewModelFactory
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            subscribeToObservers()

            if (trackingStatus == ServiceStatus.STOPPED && trackingPoints.isNotEmpty()) {
                drawFinalRoute()
            } else {
                drawRoute()
            }

        }

        binding.startRideButton.setOnClickListener {
            if (PermissionsUtil.checkPermissions(requireContext())) {
                when (trackingStatus) {
                    ServiceStatus.STARTED -> {
                        trackingStatus = ServiceStatus.PAUSED
                        operateService(ACTION_PAUSE_LOCATION_SERVICE)
                    }

                    ServiceStatus.PAUSED -> {
                        trackingStatus = ServiceStatus.STARTED
                        operateService(ACTION_START_LOCATION_SERVICE)
                    }
                    else -> {
                        trackingStatus = ServiceStatus.STARTED
                        clearMap()
                        operateService(ACTION_START_LOCATION_SERVICE)
                    }
                }
                updateUi()
            } else {
                showPermissionToast()
            }
        }
        binding.stopRideButton.setOnClickListener {
            trackingStatus = ServiceStatus.STOPPED
            updateUi()
            drawFinalRoute()
            zoomMapToSaveForDB()
        }
    }

    private fun createViewModel() {
        App.getAppComponent()?.fragmentComponent()?.inject(this)
        rideViewModel = ViewModelProvider(this, viewModelFactory)[RideViewModel::class.java]
        Log.d("RideFragment", " rideViewModel: ${rideViewModel}")
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

    private fun operateService(action: String) {
        val intent = Intent(requireContext(), LocationService::class.java)
        intent.action = action
        requireContext().startService(intent)
    }

    private fun stopRide() {
        operateService(ACTION_STOP_LOCATION_SERVICE)
    }

    override fun onMyLocationButtonClick(): Boolean {
        return true
    }

    override fun onMyLocationClick(p0: Location) {
    }

    private fun updateUi() {
        when (trackingStatus) {
            ServiceStatus.STARTED -> {
                binding.startRideButton.setImageLevel(1)
                binding.stopRideButton.visibility = GONE
            }
            ServiceStatus.PAUSED -> {
                binding.startRideButton.setImageLevel(0)
                binding.stopRideButton.visibility = VISIBLE
            }
            ServiceStatus.STOPPED -> {
                binding.startRideButton.setImageLevel(0)
                binding.stopRideButton.visibility = GONE
            }
        }
    }

    private fun updateTimer(time: String) {
        binding.durationTextView.text = time
    }

    private fun updateData(model: RideModel) {

        if (model.isUnitsMetric) {
            binding.speedTextView.text = resources.getString(R.string.km_h, model.speed.toString())
            binding.distanceTextView.text =
                resources.getString(R.string.km, model.distance.toString())
            binding.averageSpeedTextView.text =
                resources.getString(R.string.km_h, model.averageSpeed.toString())
        } else {
            binding.speedTextView.text =
                resources.getString(R.string.miles_h, (model.speed).toString())
            binding.distanceTextView.text =
                resources.getString(R.string.miles, model.distance.toString())
            binding.averageSpeedTextView.text = resources.getString(
                R.string.miles_h, model.averageSpeed.toString()
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
                    .color(ContextCompat.getColor(requireContext(), R.color.middle_blue))
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
                    .color(ContextCompat.getColor(requireContext(), R.color.middle_blue))
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
                .color(ContextCompat.getColor(requireContext(), R.color.middle_blue))
                .jointType(JointType.ROUND)
                .addAll(line)
            map.addPolyline(polylineOptions)

        }
        addStartAndFinishMarkers(map)
        moveCameraToCurrentLocation()
    }

    private fun addStartAndFinishMarkers(googleMap: GoogleMap) {
        if (trackingPoints.first().isNotEmpty()) {
            googleMap.addMarker(
                MarkerOptions()
                    .title(getString(R.string.start))
                    .position(trackingPoints.first().first())
                    .icon(
                        BitmapHelper.vectorToBitmap(
                            requireContext(),
                            R.drawable.ic_start_flag,
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
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
                            ContextCompat.getColor(requireContext(), R.color.dark_blue)
                        )
                    )
            )
        }
    }

    private fun clearMap() {
        map.clear()
    }

    private fun moveCameraToCurrentLocation() {
        if (trackingPoints.isNotEmpty() && trackingPoints.last().size > 1) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    trackingPoints.last().last(),
                    CAMERA_ZOOM_VALUE
                )
            )
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
                            (mapView?.height!! * CAMERA_ZOOM_SCALING_AFTER_STOP).toInt()
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            } else {
                val requestPermissionLauncher =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
                    }
                val requestPermissionLauncher2 =
                    registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
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



