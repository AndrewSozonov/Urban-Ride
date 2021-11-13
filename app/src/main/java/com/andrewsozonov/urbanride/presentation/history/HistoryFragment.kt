package com.andrewsozonov.urbanride.presentation.history

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.databinding.FragmentHistoryBinding
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryItemDecoration
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryRecyclerAdapter
import com.andrewsozonov.urbanride.presentation.history.adapter.IHistoryRecyclerListener
import com.andrewsozonov.urbanride.util.Constants.BUNDLE_RIDE_ID_KEY
import com.andrewsozonov.urbanride.util.Constants.RECYCLER_ITEMS_SPACING
import com.andrewsozonov.urbanride.util.DataFormatter
import java.io.OutputStream
import javax.inject.Inject


/**
 * Фрагмент с историей поездок.
 * Загружает список поездок из БД и показывает список
 *
 * @author Андрей Созонов
 */
class HistoryFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null
    private var historyRecyclerView: RecyclerView? = null
    private var historyRecyclerAdapter: HistoryRecyclerAdapter? = null
    private var listOfRides: List<Ride> = mutableListOf()
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: HistoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        createViewModel()

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        historyRecyclerView = binding.historyRecyclerView

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        historyViewModel.getRidesFromDB()
        historyViewModel.listOfRides.observe(viewLifecycleOwner, {
            setData(it)
            listOfRides = it
        })
    }

    private fun createViewModel() {
        App.getAppComponent()?.activityComponent()?.inject(this)
        historyViewModel = viewModelFactory.create(HistoryViewModel::class.java)
    }

    private fun initRecyclerView() {
        historyRecyclerView?.setHasFixedSize(true)
        historyRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerAdapter = HistoryRecyclerAdapter(object : IHistoryRecyclerListener {
            override fun onMapClick(position: Int) {
                openMapFragment(position)
            }

            override fun onShareClick(position: Int) {
                showShareSheet(position)
            }

        })
        historyRecyclerView?.addItemDecoration(
            HistoryItemDecoration(
                requireContext(),
                R.drawable.recycler_item_divider,
                RECYCLER_ITEMS_SPACING
            )
        )
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyRecyclerView?.adapter = historyRecyclerAdapter
    }

    private fun setData(rides: List<Ride>) {
        historyRecyclerAdapter?.setData(rides)
    }

    private fun openMapFragment(position: Int) {
        val bundle = bundleOf(BUNDLE_RIDE_ID_KEY to position)
        val navController = activity?.findNavController(R.id.nav_host_fragment_activity_main)
        navController?.navigate(R.id.action_navigation_history_to_mapFragment, bundle)
    }

    private fun showShareSheet(position: Int) {

        val shareMapImage = createBitmapForSharing(position)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)

            val resolver = requireActivity().contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val fos: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
            shareMapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos?.flush()
            fos?.close()

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri!!, contentValues, null, null)

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Select the app"))
        } else {
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                shareMapImage,
                null,
                null
            )
            if (path.isNotEmpty()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
                shareIntent.type = "image/*"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "Select the app"))
            }

        }
    }

    private fun createBitmapForSharing(position: Int): Bitmap {

        val mapImage: Bitmap = listOfRides[position].mapImg

        val shareMapImage = mapImage.copy(mapImage.config, true)

        val canvas = Canvas(shareMapImage)
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = resources.getColor(R.color.middle_blue)
        fillPaint.textSize = 48f
//        fillPaint.setShadowLayer(2f, 0f, 1f, Color.BLACK)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = resources.getColor(R.color.white)
        strokePaint.strokeWidth = 0.5f
        strokePaint.textSize = 48f
        fillPaint.setShadowLayer(2f, 0f, 1f, Color.BLACK)


        val horizontalMargin = 40f
        val verticalMargin = 30f
        val distanceTextBounds = Rect()
        val distance = "Distance ${listOfRides[position].distance} km"
        fillPaint.getTextBounds(
            distance,
            0,
            listOfRides[position].distance.toString().length,
            distanceTextBounds
        )
        var distanceX: Float = horizontalMargin
        val y: Float = verticalMargin + distanceTextBounds.height()
        canvas.drawText(distance, distanceX, y, fillPaint)
        canvas.drawText(distance, distanceX, y, strokePaint)

        val timeTextBounds = Rect()
        val duration: String =
            "Duration ${DataFormatter.formatTime(listOfRides[position].duration)}"
        fillPaint.getTextBounds(duration, 0, duration.length, timeTextBounds)
        val timeX = (shareMapImage.width - horizontalMargin - timeTextBounds.width())
        canvas.drawText(duration, timeX, y, fillPaint)
        canvas.drawText(duration, timeX, y, strokePaint)
        return shareMapImage
    }

    private val itemTouchHelperCallBack =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                historyViewModel.deleteRide(listOfRides[viewHolder.adapterPosition])
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}