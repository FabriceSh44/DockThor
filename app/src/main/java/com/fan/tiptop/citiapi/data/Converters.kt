package com.fan.tiptop.citiapi.data

import androidx.room.TypeConverter
import java.sql.Date

class Converters {

    @TypeConverter
    fun toCitiStationId(string: String): CitiStationId {
        return CitiStationId(string)
    }

    @TypeConverter
    fun fromCititStationId(stationId: CitiStationId): String {
        return stationId.value
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }


}