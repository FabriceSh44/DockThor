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
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getStationStatusModel(string: String): StationStatusModel {
        val model = json.decodeFromString<StationStatusModel>(string)
        return model
    }


    fun getAvailabilities(response: String, stationId: Int): String {
        var currentStation = this.m_stationIdToStation[stationId]
        if (currentStation == null) {
            val model = getStationStatusModel(response)
            for (station in model.data.stations) {
                this.m_stationIdToStation.put(station.station_id.toInt(), station)
            }
            currentStation = this.m_stationIdToStation[stationId]
            if (currentStation == null) {
                throw Exception("Unable to find station for $stationId")
            }
        }

        return "${currentStation.num_bikes_available} bikes\n${currentStation.num_ebikes_available} e-bikes\n${currentStation.num_docks_available} docks"

    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        val model = json.decodeFromString<StationInformationModel>(stationInformationContent)
        return model

    }
}