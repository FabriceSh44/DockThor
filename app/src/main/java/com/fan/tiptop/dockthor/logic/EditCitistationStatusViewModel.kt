package com.fan.tiptop.dockthor.logic

import android.content.Intent
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

@BindingAdapter("setEndIconOnClickListener")
fun setEndIconOnClickListener(inputLayout: TextInputLayout, endIconOnClickListener: View.OnClickListener) {
    inputLayout.setEndIconOnClickListener(endIconOnClickListener)
}
class EditCitistationStatusViewModel(val station: CitiStationStatus) : ViewModel() {

    private var _kernel: DockThorKernel
    val stationAddress: String
        get() = station.address

    val stationGivenNameLD:MutableLiveData<String> = MutableLiveData(station.givenName)
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
                updateIsFavorite(false)
                _kernel.removeStationFromFavorite(station)
            }
        } else {
            viewModelScope.launch {
                updateIsFavorite(true)
                _kernel.addStationToFavorite(station)
            }
        }
    }

    fun  onFavSaveClickListener()
    {
        viewModelScope.launch {
            station.givenName= stationGivenNameLD.value!!
            _kernel.updateStationName(station)
        }
    }

    fun onDirectionClick() {
        val intent = _kernel.getActionViewIntent(station)
        navigationIntentLD.value = intent
    }

}
