package com.fan.tiptop.dockthor.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.database.CitibikeStationInformationDao

class MainViewModelFactory(private val dao: CitibikeStationInformationDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw  IllegalArgumentException("Unknown MainViewModel")
    }
}