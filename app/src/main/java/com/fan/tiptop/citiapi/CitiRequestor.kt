package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.OffsetDateTime

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


    fun getAvailabilities(
        response: String,
        station: CitibikeStationInformationModel?
    ): CitiStationStatus {
        requireNotNull(station)
        val stationId = station.station_id.toInt()
        var currentStation = this.m_stationIdToStation[stationId]
        if (currentStation == null) {
            val model = getStationStatusModel(response)
            for (stationInfo in model.data.stations) {
                this.m_stationIdToStation.put(stationInfo.station_id.toInt(), stationInfo)
            }
            currentStation = this.m_stationIdToStation[stationId]
            if (currentStation != null) {
                return CitiStationStatus(
                    currentStation.num_bikes_available,
                    currentStation.num_ebikes_available,
                    currentStation.num_docks_available,
                    LocalDateTime.ofEpochSecond(
                        model.last_updated.toLong(), 0,
                        OffsetDateTime.now().getOffset()
                    ), station.name
                )
            }
        }
        throw Exception("Unable to find station for $stationId")
    }

    fun getStationStatusModel(string: String): StationStatusModel {
        return json.decodeFromString(string)
    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        return json.decodeFromString(stationInformationContent)
    }
}