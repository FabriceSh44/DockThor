package com.fan.tiptop.citiapi.data

import androidx.room.TypeConverter

class Converters {

        @TypeConverter
        fun toCitiStationId(string: String): CitiStationId {
            return CitiStationId(string)
        }

        @TypeConverter
        fun fromCititStationId(stationId: CitiStationId): String {
           return stationId.value
        }

}