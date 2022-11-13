package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "station_alarm", primaryKeys = ["stationId", "dayOfWeek"])//to be put in database
@Parcelize //to be transmitted as Argument
data class CitibikeStationAlarm(
    val stationId: Int,
    val dayOfWeek: Int,
) : Parcelable