package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.dockthor.databinding.FragmentEditCitistationStatusBinding
import com.fan.tiptop.dockthor.databinding.FragmentGeofenceSetupBinding
import com.fan.tiptop.dockthor.logic.EditCitistationStatusViewModel
import com.fan.tiptop.dockthor.logic.GeofenceSetupViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class GeofenceSetupFragment : Fragment() {
    private var _viewModel: GeofenceSetupViewModel? = null
    private var _binding: FragmentGeofenceSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGeofenceSetupBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this)[GeofenceSetupViewModel::class.java]
        binding.viewModel = _viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

    fun initialize(station: CitiStationStatus) {
        _viewModel?.initialize(station)
    }
}