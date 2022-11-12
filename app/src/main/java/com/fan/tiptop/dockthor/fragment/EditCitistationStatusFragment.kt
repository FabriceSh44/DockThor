package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitibikeMetaAlarmBean
import com.fan.tiptop.dockthor.databinding.FragmentEditCitistationStatusBinding
import com.fan.tiptop.dockthor.logic.EditCitistationStatusModelFactory
import com.fan.tiptop.dockthor.logic.EditCitistationStatusViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class EditCitistationStatusFragment : Fragment() {

    private var _binding: FragmentEditCitistationStatusBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: EditCitistationStatusViewModel? = null
    private val viewModel get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCitistationStatusBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        if (!requireArguments().isEmpty) {
            val station = EditCitistationStatusFragmentArgs.fromBundle(requireArguments()).station
            val alarms = EditCitistationStatusFragmentArgs.fromBundle(requireArguments()).alarms
            val alarmData = EditCitistationStatusFragmentArgs.fromBundle(requireArguments()).alarmData
            val viewModelFactory = EditCitistationStatusModelFactory(station)
            _viewModel = ViewModelProvider(
                this,
                viewModelFactory
            )[EditCitistationStatusViewModel::class.java]
            binding.viewModel = viewModel
            val geofenceSetupFragment =
                childFragmentManager.fragments.firstOrNull() as GeofenceSetupFragment
            geofenceSetupFragment.setArguments(station,CitibikeMetaAlarmBean(alarms.toList(),alarmData))
            viewModel.navigationIntentLD.observe(viewLifecycleOwner) { navigationIntent ->
                navigationIntent?.let { context?.startActivity(it) }
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

}
