package com.fan.tiptop.citiapi

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "station_information_model")
@Serializable
@Parcelize //to be transmitted as Argument
data class CitibikeStationInformationModel(
    val lon: Double,
    val external_id: String,
    val eightd_has_key_dispenser: Boolean,
    val short_name: String,
    val name: String,
    val station_type: String,
    val capacity: Int,
    @PrimaryKey
    val station_id: String,
    val electric_bike_surcharge_waiver: Boolean,
    val lat: Double,
    val region_id: String,
    val has_kiosk: Boolean,
    val legacy_id: String
):Parcelable