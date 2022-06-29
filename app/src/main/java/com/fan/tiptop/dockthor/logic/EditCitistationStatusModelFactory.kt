package com.fan.tiptop.dockthor.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.data.CitiStationStatus

class EditCitistationStatusModelFactory(private val station: CitiStationStatus) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCitistationStatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditCitistationStatusViewModel(station) as T
        }
        throw  IllegalArgumentException("Unknown MainViewModel")
    }
}