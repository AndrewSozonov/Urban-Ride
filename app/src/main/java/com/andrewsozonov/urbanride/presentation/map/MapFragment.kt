package com.andrewsozonov.urbanride.presentation.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.databinding.MapFragmentBinding
import com.andrewsozonov.urbanride.util.BitmapHelper
import com.andrewsozonov.urbanride.util.constants.MapConstants.CAMERA_ZOOM_SCALING_AFTER_STOP
import com.andrewsozonov.urbanride.util.constants.MapConstants.POLYLINE_WIDTH
import com.andrewsozonov.urbanride.util.constants.UIConstants.BUNDLE_RIDE_ID_KEY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import javax.inject.Inject

/**
 * Фрагмент с картой. Открывается при нажатии на картинку с картой на экране History.
 * Отображает GoogleMap на полный экран. Рисует маршрут поездки.
 *
 * @author Андрей Созонов
 */
class MapFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    private var _binding: MapFragmentBinding? = null
    private lateinit var map: GoogleMap
    private var mapView: MapView? = null
    private var trackingPoints: List<List<LatLng>> = mutableListOf()
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: MapViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MapFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = binding.mapView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync {
            map = it
            arguments?.getInt(BUNDLE_RIDE_ID_KEY)?.let { id -> viewModel.getRide(id) }
            subscribeToObservers()
        }
    }

    private fun createViewModel() {
        App.getAppComponent()?.fragmentComponent()?.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]
    }

    private fun subscribeToObservers() {
        viewModel.trackingPoints.observe(viewLifecycleOwner, {
            trackingPoints = it
            drawFinalRoute()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })
    }

    private fun showProgressBar() {
        binding.mapProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.mapProgressBar.visibility = View.GONE
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
        zoomMap()
    }

    private fun addStartAndFinishMarkers(googleMap: GoogleMap) {
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

    private fun zoomMap() {
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
        }
    }

    private fun clearMap() {
        map.clear()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
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
}