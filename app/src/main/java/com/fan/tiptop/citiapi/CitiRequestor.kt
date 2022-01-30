package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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

data class CitiRequestResult(
    val numBikeAvailable: Int,
    val numEbikeAvailable: Int,
    val numDockAvailable: Int,
    val lastUpdatedTime: LocalDateTime
)

class CitiRequestor {
    private var m_stationIdToStation: MutableMap<Int, CitibikeStationModel> = mutableMapOf()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getStationStatusModel(string: String): StationStatusModel {
        return json.decodeFromString<StationStatusModel>(string)
    }


    fun getAvailabilities(response: String, stationId: Int): CitiRequestResult {
        var currentStation = this.m_stationIdToStation[stationId]
        if (currentStation == null) {
            val model = getStationStatusModel(response)
            for (station in model.data.stations) {
                this.m_stationIdToStation.put(station.station_id.toInt(), station)
            }
            currentStation = this.m_stationIdToStation[stationId]
            if (currentStation != null) {
                return CitiRequestResult(
                    currentStation.num_bikes_available,
                    currentStation.num_ebikes_available,
                    currentStation.num_docks_available,
                    LocalDateTime.ofEpochSecond(model.last_updated.toLong(), 0,
                        OffsetDateTime.now().getOffset())
                )
            }
        }
        throw Exception("Unable to find station for $stationId")
    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        val model = json.decodeFromString<StationInformationModel>(stationInformationContent)
        return model

    }
}