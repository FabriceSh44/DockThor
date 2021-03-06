package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.dockthor.databinding.FragmentGeofenceSetupBinding
import com.fan.tiptop.dockthor.logic.GeofenceSetupModelFactory
import com.fan.tiptop.dockthor.logic.GeofenceSetupViewModel


class GeofenceSetupFragment : Fragment() {
    private  var _station: CitiStationStatus? = null
    private var _viewModel: GeofenceSetupViewModel? = null
    private var _binding: FragmentGeofenceSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGeofenceSetupBinding.inflate(inflater, container, false)
        if (_station!=null) {
            val viewModelFactory = GeofenceSetupModelFactory(_station!!)
            _viewModel = ViewModelProvider(this, viewModelFactory).get(GeofenceSetupViewModel::class.java)
            binding.viewModel = _viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

    fun setStation(station: CitiStationStatus) {
        _station = station
    }

}