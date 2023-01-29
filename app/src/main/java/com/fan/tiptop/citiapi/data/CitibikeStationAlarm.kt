package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Entity(tableName = "station_alarm", primaryKeys = ["stationId", "dayOfWeek"])//to be put in database
@Parcelize //to be transmitted as Argument
data class CitibikeStationAlarm(
    val stationId: CitiStationId,
    val dayOfWeek: Int,
) : Parcelable