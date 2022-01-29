package com.fan.tiptop.dockthor.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.citiapi.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager

class MainViewModel : ViewModel() {
    private val TAG = "DockThorModel"
     var station: CitibikeStationInformationModel=CitibikeStationInformationModel(
        0.0, "", false,
        "", "Clinton Street",
        "", 0, "4620",
        true, 0.0, "", listOf(),
        false, ""
    )
    private val _bikeAtStation= MutableLiveData("")
    val bikeAtStation: LiveData<String>
        get()=_bikeAtStation
     val switchFavStation= MutableLiveData("Pick new favorite station")

    fun refreshBikeStation() {
        NetworkManager.getInstance().stationStatusRequest(object : DefaultNetworkManagerListener {
            override fun getResult(result: String) {
                if (result.isNotEmpty()) {
                    _bikeAtStation.value= processResponse(result)
                }
            }

            override fun getError(error: String) {
                if (error.isNotEmpty()) {
                    _bikeAtStation.value= error
                }
            }
        })
    }
    fun processResponse(response: String): String {
        try {
            val requestor = CitiRequestor()
            val availabilities = requestor.getAvailabilities(response, station.station_id.toInt())
            return "${station.name}\n------\n$availabilities"
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return "NaN"
        }
    }
}