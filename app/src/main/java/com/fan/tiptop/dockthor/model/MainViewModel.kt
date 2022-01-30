package com.fan.tiptop.dockthor.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.tiptop.citiapi.CitiStationStatus
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.citiapi.CitibikeStationInformationDao
import com.fan.tiptop.citiapi.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainViewModel(val dao: CitibikeStationInformationDao) : ViewModel() {
    private val TAG = "DockThorModel"
    private var _favoriteStation: CitibikeStationInformationModel? = null
    val citiStationStatus: MutableLiveData<List<CitiStationStatus>> = MutableLiveData()
    var favoriteStationIsNull = MutableLiveData(true)
    val switchFavStation = MutableLiveData("Pick new favorite station")

    fun refreshBikeStation() {
        viewModelScope.launch {
            var favoriteStation = dao.getFavoriteStation()
            if (favoriteStation.isEmpty()) {
                Log.i(TAG, "Not favorite station defined in database")
                setFavoriteStation(null)
            } else if (favoriteStation.size > 1) {
                Log.e(TAG, "Should only have one station here, got ${favoriteStation.size}")
            } else {
                setFavoriteStation(favoriteStation[0])
                requestStationStatus()
            }
        }
    }

    private fun setFavoriteStation(station: CitibikeStationInformationModel?) {
        _favoriteStation = station
        favoriteStationIsNull.value = _favoriteStation == null
    }

    private fun requestStationStatus() {
        NetworkManager.getInstance()
            .stationStatusRequest(object : DefaultNetworkManagerListener {
                override fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        citiStationStatus.value = listOf(processResponse(result)!!)
                    }
                }

                override fun getError(error: String) {
                    if (error.isNotEmpty()) {
                        //_bikeAtStation.value = processError(error)
                    }
                }
            })
    }

    fun processError(error: String): String {
        return error;
    }

    fun processResponse(response: String): CitiStationStatus? {
        try {
            if (_favoriteStation != null) {
                val requestor = CitiRequestor()
                val stationStatus: CitiStationStatus =
                    requestor.getAvailabilities(response, _favoriteStation)
                return stationStatus
            }
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return null
        }
    }

    fun setStation(stationModel: CitibikeStationInformationModel) {
        viewModelScope.launch {
            var listStation = dao.getFavoriteStation()
            for (station in listStation) {
                dao.delete(station)
            }
            dao.insert(stationModel)
            refreshBikeStation()
        }
    }
}