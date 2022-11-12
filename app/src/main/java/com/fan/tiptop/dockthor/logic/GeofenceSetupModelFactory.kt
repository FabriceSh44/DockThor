package com.fan.tiptop.dockthor.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeMetaAlarmBean

class GeofenceSetupModelFactory(private val station: CitiStationStatus, private val alarmBean: CitibikeMetaAlarmBean) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeofenceSetupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeofenceSetupViewModel(station, alarmBean) as T
        }
        throw  IllegalArgumentException("Unknown MainViewModel")
    }
}