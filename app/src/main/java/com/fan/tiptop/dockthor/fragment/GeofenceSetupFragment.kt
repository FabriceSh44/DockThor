package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import com.fan.tiptop.dockthor.R
import com.fan.tiptop.dockthor.databinding.FragmentGeofenceSetupBinding
import com.fan.tiptop.dockthor.logic.GeofenceSetupModelFactory
import com.fan.tiptop.dockthor.logic.GeofenceSetupViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class GeofenceSetupFragment : Fragment() {
    private  var _station: CitiStationStatus? = null
    private  var _alarms: List<CitibikeStationAlarm>? = null
    private var _viewModel: GeofenceSetupViewModel? = null
    private var _binding: FragmentGeofenceSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGeofenceSetupBinding.inflate(inflater, container, false)
        if (_station!=null) {
            val viewModelFactory = GeofenceSetupModelFactory(_station!!, _alarms!!)
            _viewModel = ViewModelProvider(this, viewModelFactory)[GeofenceSetupViewModel::class.java]
            binding.viewModel = _viewModel
            viewModel.messageToDisplayLD.observe(viewLifecycleOwner) { errorText ->
                if (errorText.isNotEmpty()) {
                    Toast.makeText(binding.root.context, errorText, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.startTimeClickedLD.observe(viewLifecycleOwner) { startTimeClicked ->
                if (startTimeClicked) {
                    viewModel.startTimeClickedLD.value=false
                    val picker =
                        MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(viewModel.startTimeHour)
                            .setMinute(viewModel.startTimeMinute)
                            .setTitleText("Select start time")
                            .setTheme(R.style.Theme_DockThor_TimePicker)
                            .build()
                    picker.show(this.parentFragmentManager, "tag")
                    picker.addOnPositiveButtonClickListener {
                        viewModel.updateStartTime(
                            picker.hour,
                            picker.minute
                        )
                    }
                }
            }
            viewModel.endTimeClickedLD.observe(viewLifecycleOwner) { endTimeClicked ->
                if (endTimeClicked) {
                    viewModel.endTimeClickedLD.value=false
                    val picker =
                        MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setHour(viewModel.endTimeHour)
                            .setMinute(viewModel.endTimeMinute)
                            .setTitleText("Select end time")
                            .setTheme(R.style.Theme_DockThor_TimePicker)
                            .build()
                    picker.show(this.parentFragmentManager, "tag")
                    picker.addOnPositiveButtonClickListener {
                        viewModel.updateEndTime(
                            picker.hour,
                            picker.minute
                        )
                    }
                }
            }
        }
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

    fun setArguments(station: CitiStationStatus, alarms:List<CitibikeStationAlarm>) {
        _station = station
        _alarms = alarms
    }

}