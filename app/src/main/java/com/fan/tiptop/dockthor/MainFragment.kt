package com.fan.tiptop.dockthor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fan.tiptop.dockthor.databinding.FragmentMainBinding
import com.fan.tiptop.dockthor.model.MainViewModel


class MainFragment : Fragment() {

    //Mainfragment works with databinding
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var _mainViewModel : MainViewModel? =null
    private val mainViewModel get() = _mainViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        _mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val view = binding.root
        if (!requireArguments().isEmpty) {
            mainViewModel.station = MainFragmentArgs.fromBundle(requireArguments()).stationModel
        }
        binding.switchFavStationButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_stationSearchFragment)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.refreshBikeStation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _mainViewModel=null
    }


}
