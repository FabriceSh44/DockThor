package com.fan.tiptop.dockthor.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.tiptop.citiapi.CitiRequestor
import com.fan.tiptop.citiapi.CitiStationStatus
import com.fan.tiptop.citiapi.CitibikeStationInformationDao
import com.fan.tiptop.citiapi.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import kotlinx.coroutines.launch

class MainViewModel(val dao: CitibikeStationInformationDao) : ViewModel() {
    private val _navigateToSwitchFavStation = MutableLiveData(false)
    val navigateToSwitchFavStation: LiveData<Boolean>
        get() = _navigateToSwitchFavStation
    private val TAG = "DockThorViewModel"
    private var _favoriteStations: List<CitibikeStationInformationModel> = listOf()
    val citiStationStatus: MutableLiveData<List<CitiStationStatus>> = MutableLiveData()
    val _selectedStationsId: MutableLiveData<List<Int>> = MutableLiveData()
    var favoriteStationsIsEmpty = MutableLiveData(true)
    val switchFavStation = MutableLiveData("Pick new favorite station")

    fun refreshBikeStation() {
        viewModelScope.launch {
            var localFavoriteStations = dao.getFavoriteStations()
            if (localFavoriteStations.isEmpty()) {
                Log.i(TAG, "Not favorite station defined in database")
                setFavoriteStationsList(listOf())
            } else {
                setFavoriteStationsList(localFavoriteStations)
                updateCitiStationStatusToDisplay()
            }
        }
    }

    private fun setFavoriteStationsList(stationInfoList: List<CitibikeStationInformationModel>) {
        _favoriteStations = stationInfoList
        favoriteStationsIsEmpty.value = _favoriteStations.isEmpty()
    }

    private fun updateCitiStationStatusToDisplay() {
        NetworkManager.getInstance()
            .stationStatusRequest(object : DefaultNetworkManagerListener {
                override fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        citiStationStatus.value = getCitiStationStatusToDisplay(result)
                    }
                }

                override fun getError(error: String) {
                    if (error.isNotEmpty()) {
                        //TODOFE keep old value but display error (toast or icon)
                    }
                }
            })
    }

    fun getCitiStationStatusToDisplay(response: String): List<CitiStationStatus> {
        try {
            if (_favoriteStations.isNotEmpty()) {
                val requestor = CitiRequestor()
                return requestor.getAvailabilities(response, _favoriteStations)
            }
            return listOf()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return listOf()
        }
    }

    fun setStation(stationModel: CitibikeStationInformationModel) {
        viewModelScope.launch {
            dao.insert(stationModel)
            refreshBikeStation()
        }
    }

    fun onSwitchFavButtonClicked() {
        _navigateToSwitchFavStation.value = true;
    }

    fun onSwitchFavButtonNavigated() {
        _navigateToSwitchFavStation.value = false;
    }

    fun onItemDeleted() {
        if (_selectedStationsId.value == null) {
            return
        }
        viewModelScope.launch {
            for (stationId in _selectedStationsId.value!!) {
                dao.deleteByStationId(stationId)
            }
            refreshBikeStation()
        }
    }

    fun addSelectedStationId(stationId: Int) {
        _selectedStationsId.value= _selectedStationsId.value?.plus(stationId) ?: listOf(stationId)
    }
}
