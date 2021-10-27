package com.andrewsozonov.urbanride.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.adapter.HistoryItemDecoration
import com.andrewsozonov.urbanride.adapter.HistoryRecyclerAdapter
import com.andrewsozonov.urbanride.databinding.FragmentHistoryBinding
import com.andrewsozonov.urbanride.database.Ride
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

class HistoryFragment : Fragment() {

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null
    private var historyRecyclerView: RecyclerView? = null
    private var historyRecyclerAdapter: HistoryRecyclerAdapter? = null
    private var listOfRides: List<Ride> = mutableListOf()
    private lateinit var itemTouchHelper: ItemTouchHelper

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("HistoryFragment", "OnSaveInstanceState")
    }

    private fun initRecyclerView() {
        historyRecyclerView?.setHasFixedSize(true)
        historyRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerAdapter = HistoryRecyclerAdapter()
        historyRecyclerView?.addItemDecoration(HistoryItemDecoration(requireContext(), R.drawable.recycler_item_divider, 20))
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyRecyclerView?.adapter = historyRecyclerAdapter
    }

    private fun setData(rides: List<Ride>) {
        historyRecyclerAdapter?.setData(rides)

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