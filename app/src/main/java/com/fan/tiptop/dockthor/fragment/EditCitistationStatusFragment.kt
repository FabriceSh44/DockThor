package com.fan.tiptop.dockthor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.dockthor.databinding.FragmentEditCitistationStatusBinding
import com.fan.tiptop.dockthor.logic.EditCitistationStatusViewModel


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
        _viewModel = ViewModelProvider(this)[EditCitistationStatusViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.navigationIntentLD.observe(viewLifecycleOwner) { navigationIntent ->
            navigationIntent?.let { context?.startActivity(it) }
        }

        if (!requireArguments().isEmpty) {
            val station = EditCitistationStatusFragmentArgs.fromBundle(requireArguments()).station
            viewModel.initialize(station)
            binding.geofenceSetupfragmentContainerView.getFragment<GeofenceSetupFragment>().initialize(station)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
    }

}