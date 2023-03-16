package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Entity(tableName = "station_information_model")//to be put in database
@Parcelize //to be transmitted as Argument
data class StationInformationModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val station_id: CitiStationId,
    val name: String,
    val address: String,
    val isFavorite:Boolean,
    val expiryTime:Date?,
    val capacity: Int,
    val lon: Double,
    val lat: Double,
):Parcelable

