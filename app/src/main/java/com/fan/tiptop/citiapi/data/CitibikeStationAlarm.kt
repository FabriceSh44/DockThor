package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "station_alarm")//to be put in database
@Serializable //to be en/decoded in json
@Parcelize //to be transmitted as Argument
data class CitibikeStationAlarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0L,
    val stationId: Int,
    val dayOfWeek: Int,
    val hourOfDay: Int,
    val minuteOfDay: Int,
    val delayInSec: Long,
) : Parcelable