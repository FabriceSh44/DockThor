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
    private val _contextualBarNotVisible = MutableLiveData(true)
    val contextualBarNotVisible: LiveData<Boolean>
        get() = _contextualBarNotVisible
    val errorToDisplay = MutableLiveData("")
    val isLoading = MutableLiveData(false)
    private val _navigateToSwitchFavStation = MutableLiveData(false)
    val navigateToSwitchFavStation: LiveData<Boolean>
        get() = _navigateToSwitchFavStation
    private val TAG = "DockThorViewModel"
    private var _favoriteStations: List<CitibikeStationInformationModel> = listOf()
    val citiStationStatus: MutableLiveData<List<CitiStationStatus>> = MutableLiveData()
    val _selectedStationsId: MutableLiveData<List<Int>> = MutableLiveData()

    fun refreshBikeStation() {
        viewModelScope.launch {
            internalRefreshBikeStation()
        }
    }

    fun onSwipeRefresh() {
        refreshBikeStation()
    }

    private suspend fun internalRefreshBikeStation() {
        isLoading.value = true
        var localFavoriteStations = dao.getFavoriteStations()
        if (localFavoriteStations.isEmpty()) {
            errorToDisplay.value = "No favorite station available, please pick one."
            _favoriteStations = listOf()
            citiStationStatus.value = listOf()
        } else {
            _favoriteStations = localFavoriteStations
            updateCitiStationStatusToDisplay()
        }
        isLoading.value = false
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
                        errorToDisplay.value = "Unable to retrieve Citibike station status"
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
            internalRefreshBikeStation()
        }
    }

    fun onAddFavStationClicked() {
        _navigateToSwitchFavStation.value = true;
    }

    fun onAddFavStationNavigated() {
        _navigateToSwitchFavStation.value = false;
    }

    fun onItemDeleted() {
        if (_selectedStationsId.value == null) {
            return
        }
        viewModelScope.launch {
            dao.deleteByStationId((_selectedStationsId.value!!.distinct()))
            internalRefreshBikeStation()
            _contextualBarNotVisible.value = true
        }
    }

    fun clearSelectedStation() {
        citiStationStatus.value =
            citiStationStatus.value?.let { toggleSelectedStatus(it, { x -> true }) { x -> false } }
        _selectedStationsId.value = listOf()
        _contextualBarNotVisible.value = true;
    }

    fun containsModel(stationModel: CitibikeStationInformationModel): Boolean {
        return stationModel.station_id in _favoriteStations.map { x -> x.station_id }
    }

    private fun toggleSelectedStatus(
        currentList: List<CitiStationStatus>,
        stationSelector: (CitiStationStatus) -> Boolean,
        actionOnSelected: (CitiStationStatus) -> Boolean
    ): List<CitiStationStatus> {
        return currentList.map { x ->
            if (stationSelector(x)) {
                val newX = x.copy(); newX.selected = actionOnSelected(x); newX
            } else x
        }
    }

    private fun getSelectedStatus(
        currentList: List<CitiStationStatus>,
        filter: (CitiStationStatus) -> Boolean
    ): List<Boolean> {
        return currentList.filter { x -> filter(x) }.map { x -> x.selected }
    }

    fun actionLongClick(station: CitiStationStatus) {
        toggleSelectedStation(station)
    }

    private fun toggleSelectedStation(station: CitiStationStatus) {
        val currentList: List<CitiStationStatus> = citiStationStatus.value ?: return
        citiStationStatus.value =
            toggleSelectedStatus(
                currentList,
                { x -> x.stationId == station.stationId }) { x -> !x.selected }
        var isSelected =
            getSelectedStatus(citiStationStatus.value!!) { x -> x.stationId == station.stationId }.first()
        if (_selectedStationsId.value == null) {
            if (isSelected) {
                _selectedStationsId.value = listOf(station.stationId)
            }
        } else {
            if (isSelected) {
                _selectedStationsId.value =
                    _selectedStationsId.value!!.toMutableList() + station.stationId
            } else {
                _selectedStationsId.value =
                    _selectedStationsId.value!!.toMutableList() - station.stationId
            }
        }
        _contextualBarNotVisible.value =
            _selectedStationsId.value == null || _selectedStationsId.value!!.isEmpty()
    }

    fun actionClick(station: CitiStationStatus) {
        if(!contextualBarNotVisible.value!!){toggleSelectedStation(station)}
    }

}
