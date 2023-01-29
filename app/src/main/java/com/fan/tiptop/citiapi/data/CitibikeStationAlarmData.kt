package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "station_alarm_data")//to be put in database
@Parcelize //to be transmitted as Argument
data class CitibikeStationAlarmData(
    @PrimaryKey(autoGenerate = false)
    val stationId: CitiStationId,
    var activated: Boolean,
    val hourOfDay: Int,
    val minuteOfDay: Int,
    val delayInSec: Long,
    val dockThreshold: Int,
    var wakeUpTimeInMillis: Long
) : Parcelable