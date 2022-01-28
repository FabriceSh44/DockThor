package com.fan.tiptop.dockthor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.dockthor.databinding.FragmentMainBinding
import com.fan.tiptop.dockthor.network.NetworkManager
import com.fan.tiptop.dockthor.network.SomeCustomListener


class MainFragment : Fragment() {

    private val TAG = "DockThor"

    //my default favorit station id
    private var _stationId = 4620
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!requireArguments().isEmpty) {
            _stationId = MainFragmentArgs.fromBundle(requireArguments()).stationId
        }
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.bikeAtStationButton.setOnClickListener { onBikeAtStationClick(binding.bikeAtStationButton) }
        binding.switchFavStationButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_stationSearchFragment)
        }
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    fun onBikeAtStationClick(bikeAtStationButton: Button) {
        NetworkManager.getInstance().stationStatusRequest(object : SomeCustomListener {
            override fun getResult(result: String) {
                if (!result.isEmpty()) {
                    bikeAtStationButton.text = processResponse(result)
                }
            }

            override fun getError(error: String) {
                if (!error.isEmpty()) {
                    bikeAtStationButton.text = error
                }
            }
        })
    }


    fun processResponse(response: String): String {
        try {
            val requestor = CitiRequestor()
            val availabilities = requestor.getAvailabilities(response, _stationId)
            return "$availabilities"
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return "NaN"
        }
    }

}
