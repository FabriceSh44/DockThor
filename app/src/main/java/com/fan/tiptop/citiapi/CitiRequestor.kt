package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Stations(val stations: List<CitibikeStationModel>)

@Serializable
data class StationStatusModel(val data: Stations, val last_updated: Int, val ttl: Int)

@Serializable
data class RentalUri(val ios: String, val android: String)

@Serializable
data class StationInformations(val stations: List<CitibikeStationInformationModel>)


@Serializable
data class StationInformationModel(
    val data: StationInformations,
    val last_updated: Int,
    val ttl: Int
)

class CitiRequestor {
    private var m_stationIdToStation: MutableMap<Int, CitibikeStationModel> = mutableMapOf()
    fun getStationStatusModel(string: String): StationStatusModel {
        val model = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<StationStatusModel>(string)
        return model
    }


    fun getAvailabilities(response: String, stationId: Int): String {
        var station = this.m_stationIdToStation[stationId]
        if (station == null) {
            val model = getStationStatusModel(response)
            for (station in model.data.stations) {
                this.m_stationIdToStation.put(station.station_id.toInt(), station)
            }
            station = this.m_stationIdToStation[stationId]
            if (station == null) {
                throw Exception("Unable to find station for $stationId")
            }
        }

        return "${station.num_bikes_available} bikes\n${station.num_docks_available} docks"

    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        val model = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<StationInformationModel>(stationInformationContent)
        return model

    }
}