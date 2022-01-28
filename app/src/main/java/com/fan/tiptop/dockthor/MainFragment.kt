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
import com.fan.tiptop.dockthor.network.NetworkManager
import com.fan.tiptop.dockthor.network.SomeCustomListener


class MainFragment : Fragment() {

    private val TAG = "DockThor"
    //my default favorit station id
    private var _stationId = 4620

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val bikeAtStationButton = view.findViewById<Button>(R.id.bike_at_station_button)
        bikeAtStationButton.setOnClickListener {
            onBikeAtStationClick(bikeAtStationButton)
        }
        val switchFavStationButton = view.findViewById<Button>(R.id.switch_fav_station_button)
        switchFavStationButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_stationSearchFragment)
        }
        if (!requireArguments().isEmpty) {
            _stationId = MainFragmentArgs.fromBundle(requireArguments()).stationId
        }
        return view
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
