package com.fan.tiptop.dockthor.logic

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.tiptop.citiapi.data.CitiStationStatus
import kotlinx.coroutines.launch

class EditCitistationStatusViewModel : ViewModel() {

    private lateinit var _kernel: DockThorKernel
    private var _station: CitiStationStatus? = null
    val station: CitiStationStatus
        get() = _station!!
    val stationAddress: String?
        get() = _station?.address
    private var _isStationFavoriteLD = MutableLiveData(false)
    val isStationFavoriteLD: LiveData<Boolean>
        get() = _isStationFavoriteLD

    val navigationIntentLD = MutableLiveData<Intent?>(null)
    fun initialize(station: CitiStationStatus) {
        _station = station
        updateIsFavorite(station.isFavorite)
        _isStationFavoriteLD.value = station.isFavorite
        _kernel = DockThorKernel.getInstance()
    }

    fun updateIsFavorite(value: Boolean) {
        _station?.let { it.isFavorite = value }
        _isStationFavoriteLD.value = value
    }

    fun onFavoriteClick() {
        _station?.let {
            if (it.isFavorite) {
                viewModelScope.launch {
                    _kernel.removeStationFromFavorite(it);
                    updateIsFavorite(false)
                }
            } else {
                viewModelScope.launch {
                    _kernel.addStationToFavorite(it)
                    updateIsFavorite(true)
                }
            }
        }
    }
    fun onSwitchClick(){
        _station?.let{_kernel.addGeofenceToStation(it)}
    }

    fun onDirectionClick() {
        _station?.let {
            var intent = _kernel.getActionViewIntent(it)
            navigationIntentLD.value = intent
        }
    }
}
