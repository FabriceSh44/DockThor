package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize //to be transmitted as Argument
data class CitibikeMetaAlarmBean(
    var alarms: List<CitibikeStationAlarm>,
    var alarmData: CitibikeStationAlarmData
) : Parcelable {
    @IgnoredOnParcel
    val nextAlarm: CitibikeStationAlarm?
        get() {
            return alarms.firstOrNull()
        }
}
