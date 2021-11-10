package com.andrewsozonov.urbanride.presentation.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryItemDecoration
import com.andrewsozonov.urbanride.presentation.history.adapter.HistoryRecyclerAdapter
import com.andrewsozonov.urbanride.presentation.history.adapter.IHistoryRecyclerListener
import com.andrewsozonov.urbanride.app.App
import com.andrewsozonov.urbanride.databinding.FragmentHistoryBinding
import com.andrewsozonov.urbanride.database.Ride
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
        historyViewModel.listOfRides.observe(viewLifecycleOwner, Observer {
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
        historyRecyclerAdapter = HistoryRecyclerAdapter(object: IHistoryRecyclerListener {
            override fun onMapClick(position: Int) {
                openMapFragment(position)
            }

        })
        historyRecyclerView?.addItemDecoration(HistoryItemDecoration(requireContext(), R.drawable.recycler_item_divider, 20))
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyRecyclerView?.adapter = historyRecyclerAdapter
    }

    private fun setData(rides: List<Ride>) {
        historyRecyclerAdapter?.setData(rides)

    }

    private fun openMapFragment(position: Int) {
        Log.d("openMapFragment", " position: $position")
        val bundle = bundleOf("ID_KEY" to position)
        val navController = activity?.findNavController(R.id.nav_host_fragment_activity_main)
        navController?.navigate(R.id.action_navigation_history_to_mapFragment, bundle)
    }

    private val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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