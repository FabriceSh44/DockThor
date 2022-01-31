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


    fun getAvailabilities(
        response: String,
        stationList: List<CitibikeStationInformationModel>
    ): List<CitiStationStatus> {
        var result = mutableListOf<CitiStationStatus>()
        if (stationList.isEmpty()) {
            return result
        }
        if (this.m_stationIdToStation.isEmpty()) {
            val model = getStationStatusModel(response)
            for (stationInfo in model.data.stations) {
                this.m_stationIdToStation.put(stationInfo.station_id.toInt(), stationInfo)
            }
        }
        for (stationInfoMode in stationList) {
            val stationId = stationInfoMode.station_id.toInt()
            val stationStatus = this.m_stationIdToStation[stationId]
            if (stationStatus == null) {
                throw Exception("Unable to find station for $stationId")
            }
            result.add(
                CitiStationStatus(
                    stationStatus.num_bikes_available,
                    stationStatus.num_ebikes_available,
                    stationStatus.num_docks_available,
                    stationInfoMode.name
                )
            )
        }
        return result
    }


    fun getStationStatusModel(string: String): StationStatusModel {
        return json.decodeFromString(string)
    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        return json.decodeFromString(stationInformationContent)
    }
}