package com.fan.tiptop.dockthor.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fan.tiptop.citiapi.CitibikeStationInformationDao

class MainViewModelFactory(private val dao: CitibikeStationInformationDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dao) as T
        }
        throw  IllegalArgumentException("Unknown MainViewModel")
    }
}