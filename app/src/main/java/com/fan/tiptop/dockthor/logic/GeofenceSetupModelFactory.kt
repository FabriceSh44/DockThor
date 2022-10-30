package com.fan.tiptop.dockthor.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm

class GeofenceSetupModelFactory(private val station: CitiStationStatus, private val alarms: List<CitibikeStationAlarm>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeofenceSetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeofenceSetupViewModel(station, alarms) as T
        }
        throw  IllegalArgumentException("Unknown MainViewModel")
    }
}