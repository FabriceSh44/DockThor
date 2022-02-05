package com.fan.tiptop.dockthor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fan.tiptop.citiapi.DockThorDatabase
import com.fan.tiptop.dockthor.adapter.CitiStationStatusAdapter
import com.fan.tiptop.dockthor.databinding.FragmentMainBinding
import com.fan.tiptop.dockthor.model.MainViewModel
import com.fan.tiptop.dockthor.model.MainViewModelFactory


class MainFragment : Fragment() {

    //Mainfragment works with databinding
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var _mainViewModel: MainViewModel? = null
    private val mainViewModel get() = _mainViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        //set main view model
        val application = requireNotNull(this.activity).application
        val dao = DockThorDatabase.getInstance(application).citibikeStationInformationDao

        val viewModelFactory = MainViewModelFactory(dao)
        _mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val adapter = CitiStationStatusAdapter({ stationId: Int ->
            setContextualBar(stationId)
        }, { stationId: Int ->
            Boolean
            setContextualBar(stationId)
            true
        }
        )
        binding.citibikeStatusList.adapter = adapter
        binding.citibikeStatusList.layoutManager = GridLayoutManager(view.context, 2)

        // this connect the model citistation status to the adapter which setup view
        mainViewModel.citiStationStatus.observe(
            viewLifecycleOwner
        ) { it?.let { adapter.submitList(it) } }

        //switch fave station behavior
        binding.switchFavStationButton.setOnClickListener {
            mainViewModel.onSwitchFavButtonClicked()
        }
        mainViewModel.navigateToSwitchFavStation.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                view.findNavController().navigate(R.id.action_mainFragment_to_stationSearchFragment)
                mainViewModel.onSwitchFavButtonNavigated()
            }
        }
        if (!requireArguments().isEmpty) {
            mainViewModel.setStation(MainFragmentArgs.fromBundle(requireArguments()).stationModel)
        }

        return view
    }

    // This should set the context action bar TODOFE
    private fun setContextualBar(stationId: Int) {
        Toast.makeText(binding.root.context, "Clicked station ${stationId}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.refreshBikeStation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _mainViewModel = null
    }


}
