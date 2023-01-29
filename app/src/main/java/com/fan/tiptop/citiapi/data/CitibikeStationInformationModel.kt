package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "station_information_model")//to be put in database
@Serializable //to be en/decoded in json
@Parcelize //to be transmitted as Argument
data class CitibikeStationInformationModel(
    @PrimaryKey
    val station_id: String,
    val name: String,
    val capacity: Int,
    val lon: Double,
    val lat: Double,
):Parcelable

