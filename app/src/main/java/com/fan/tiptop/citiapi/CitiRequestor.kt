package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Stations(val stations: List<CitibikeStationModel>)

@Serializable
data class StationStatusModel(val data: Stations, val last_updated: Int, val ttl: Int)

@Serializable
data class StationInformations(val stations: List<CitibikeStationInformationModel>)


@Serializable
data class StationInformationModel(
    val data: StationInformations,
    val last_updated: Int,
    val ttl: Int
)

class CitiRequestor {
    private var _stationIdToStation: MutableMap<Int, CitibikeStationModel> = mutableMapOf()
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
        if (this._stationIdToStation.isEmpty()) {
            val model = getStationStatusModel(response)
            for (stationInfo in model.data.stations) {
                this._stationIdToStation.put(stationInfo.station_id.toInt(), stationInfo)
            }
        }
        for (stationInfoModel in stationList) {
            val stationId = stationInfoModel.station_id.toInt()
            val stationStatus = this._stationIdToStation[stationId]
                ?: throw Exception("Unable to find station for $stationId")
            result.add(
                CitiStationStatus(
                    stationStatus.num_bikes_available.toString(),
                    stationStatus.num_ebikes_available.toString(),
                    stationStatus.num_docks_available.toString(),
                    stationInfoModel.name,
                    stationInfoModel.station_id.toInt()
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