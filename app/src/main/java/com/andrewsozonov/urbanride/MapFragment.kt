package com.andrewsozonov.urbanride

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andrewsozonov.urbanride.databinding.FragmentHistoryBinding
import com.andrewsozonov.urbanride.databinding.MapFragmentBinding

class MapFragment : Fragment() {

   /* companion object {
        fun newInstance() = MapFragment()
    }*/

    private var _binding: MapFragmentBinding? = null

    private lateinit var viewModel: MapViewModel
    private val binding get() = _binding!!
    private var textView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        return inflater.inflate(R.layout.map_fragment, container, false)

        textView = binding.mapTextView
        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView?.text = "id: ${arguments?.getInt("ID_KEY").toString()}"
    }

}