package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// this dataclass contains everything necessary to draw a tile
@Parcelize //to be transmitted as Argument
data class CitiStationStatus(
    // retrieved from citi status api request
    val numBikeAvailable: String,
    val numEbikeAvailable: String,
    val numDockAvailable: String,
    // retrieved from citi information api request
    var address: String = "",
    var stationId: CitiStationId = CitiStationId(""),
    //retrieved from gps data
    var distance: String? = null,
    // retrieved from database
    var isFavorite: Boolean = false,
    var givenName: String = "",
    //changed by user
    var selected: Boolean = false
) : Parcelable {
    fun fillFromStationModel(model: StationInformationModel) {
        stationId = model.station_id
        address = model.address
        if (model.name.isNullOrEmpty())
            givenName = "Fav${model.id}"
        else
            givenName = model.name
    }
}
