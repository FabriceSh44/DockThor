package com.fan.tiptop.dockthor.logic

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.tiptop.citiapi.data.CitiStationStatus
import kotlinx.coroutines.launch

class EditCitistationStatusViewModel(val station: CitiStationStatus) : ViewModel() {

    private  var _kernel: DockThorKernel
    val stationAddress: String
        get() = station.address
    private var _isStationFavoriteLD = MutableLiveData(false)
    val isStationFavoriteLD: LiveData<Boolean>
        get() = _isStationFavoriteLD

    val navigationIntentLD = MutableLiveData<Intent?>(null)

    init {
        _isStationFavoriteLD.value = station.isFavorite
        _kernel = DockThorKernel.getInstance()
        updateIsFavorite(station.isFavorite)
    }

    private fun updateIsFavorite(value: Boolean) {
        station.isFavorite = value
        _isStationFavoriteLD.value = value
    }

    fun onFavoriteClick() {
        if (station.isFavorite) {
            viewModelScope.launch {
                _kernel.removeStationFromFavorite(station);
                updateIsFavorite(false)
            }
        } else {
            viewModelScope.launch {
                _kernel.addStationToFavorite(station)
                updateIsFavorite(true)
            }
        }
    }

    fun onSwitchClick() {
        _kernel.addGeofenceToStation(station)
    }

    fun onDirectionClick() {
        var intent = _kernel.getActionViewIntent(station)
        navigationIntentLD.value = intent
    }
}
