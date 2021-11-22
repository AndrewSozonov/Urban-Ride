package com.andrewsozonov.urbanride.presentation.history

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
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
import com.andrewsozonov.urbanride.databinding.FragmentHistoryBinding
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryItemDecoration
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryRecyclerAdapter
import com.andrewsozonov.urbanride.presentation.history.adapter.IHistoryRecyclerListener
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel
import com.andrewsozonov.urbanride.util.constants.UIConstants.BUNDLE_RIDE_ID_KEY
import com.andrewsozonov.urbanride.util.constants.UIConstants.RECYCLER_ITEMS_SPACING
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
    private var historyRecyclerAdapter: HistoryRecyclerAdapter? = null
    private var listOfRides: List<HistoryModel> = mutableListOf()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        historyViewModel.getRidesFromDB()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        historyViewModel.listOfRides.observe(viewLifecycleOwner, {
            setData(it)
            listOfRides = it
        })

        historyViewModel.isLoading.observe(viewLifecycleOwner, {
            if (it) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })
    }

    private fun createViewModel() {
        App.getAppComponent()?.activityComponent()?.inject(this)
        historyViewModel = viewModelFactory.create(HistoryViewModel::class.java)
    }

    private fun initRecyclerView() {
        binding.historyRecyclerView.setHasFixedSize(true)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerAdapter = HistoryRecyclerAdapter(object : IHistoryRecyclerListener {
            override fun onMapClick(position: Int) {
                openMapFragment(position)
            }

            override fun onShareClick(position: Int) {
                showShareSheet(position)
            }

        })
        binding.historyRecyclerView.addItemDecoration(
            HistoryItemDecoration(
                requireContext(),
                R.drawable.recycler_item_divider,
                RECYCLER_ITEMS_SPACING
            )
        )
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.historyRecyclerView)
        binding.historyRecyclerView.adapter = historyRecyclerAdapter
    }

    private fun setData(rides: List<HistoryModel>) {
        historyRecyclerAdapter?.setData(rides)
    }

    private fun openMapFragment(position: Int) {
        val bundle = bundleOf(BUNDLE_RIDE_ID_KEY to position)
        val navController = activity?.findNavController(R.id.nav_host_fragment_activity_main)
        navController?.navigate(R.id.action_navigation_history_to_mapFragment, bundle)
    }

    private fun showProgressBar() {
        binding.historyProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.historyProgressBar.visibility = View.GONE
    }

    private fun showShareSheet(position: Int) {
        val bitmapConstructor = BitmapConstructor(requireContext())
        val shareMapImage = bitmapConstructor.constructBitmapForSharing(listOfRides[position])

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.Images.Media.MIME_TYPE,
                resources.getString(R.string.content_values_mime_type)
            )
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)

            val resolver = requireActivity().contentResolver
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val fos: OutputStream? = imageUri?.let { resolver.openOutputStream(it) }
            shareMapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos?.flush()
            fos?.close()

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri!!, contentValues, null, null)

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.type = resources.getString(R.string.share_intent_type)
            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            shareIntent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            startActivity(Intent.createChooser(shareIntent, null))
        } else {
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                shareMapImage,
                null,
                null
            )
            if (path.isNotEmpty()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
                shareIntent.type = resources.getString(R.string.share_intent_type)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, null))
            }
        }
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
                historyViewModel.deleteRide(
                    listOfRides[viewHolder.adapterPosition].id,
                )
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}