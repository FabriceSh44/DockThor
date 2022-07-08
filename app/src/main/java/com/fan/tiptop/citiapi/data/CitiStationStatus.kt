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
    var address: String,
    val stationId: Int,
    //retrieved from gps data
    var distance:String?=null,
    // retrieved from database
    var isFavorite: Boolean=false,
    //changed by user
    var selected: Boolean=false
): Parcelable
