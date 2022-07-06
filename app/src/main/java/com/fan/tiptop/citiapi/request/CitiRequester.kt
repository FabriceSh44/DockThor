package com.fan.tiptop.citiapi

import com.fan.tiptop.citiapi.data.*
import com.fan.tiptop.citiapi.location.LocationUtils
import com.fan.tiptop.dockthor.R
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

class CitiRequester {
    private var _stationIdToStation: MutableMap<Int, CitibikeStationModel> = mutableMapOf()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getAvailabilitiesWithLocation(
        response: String,
        stationList: List<CitibikeStationInformationModelDecorated>,
        userLocation: Location?
    ): List<CitiStationStatus> {
        var result = mutableListOf<CitiStationStatus>()
        if (stationList.isEmpty()) {
            return result
        }
        val model = getStationStatusModel(response)
        for (stationInfo in model.data.stations) {
            this._stationIdToStation.put(stationInfo.station_id.toInt(), stationInfo)
        }
        for (stationInfoModelDecorated in stationList) {
            val stationInfoModel = stationInfoModelDecorated.model
            val stationId = stationInfoModel.station_id.toInt()
            val stationStatus = this._stationIdToStation[stationId]
                ?: throw Exception("Unable to find station for $stationId")
            var distanceDescription = ""
            if (userLocation != null) {
                distanceDescription = LocationUtils.computeAndFormatDistance(
                    userLocation.latitude,
                    userLocation.longitude,
                    stationInfoModel.lat,
                    stationInfoModel.lon
                )
            }
            result.add(
                CitiStationStatus(
                    stationStatus.num_bikes_available.toString(),
                    stationStatus.num_ebikes_available.toString(),
                    stationStatus.num_docks_available.toString(),
                    stationInfoModel.name,
                    stationInfoModel.station_id.toInt(),
                    distanceDescription,
                    isFavorite = stationInfoModelDecorated.isFavorite
                )
            )
        }
        return result
    }

    fun getStationIdWithCriteria(response: String, criteria: StationSearchCriteria): Set<Int> {
        val model = getStationStatusModel(response)
        var result = mutableSetOf<Int>()
        for (stationStatus in model.data.stations) {
            if (criteria == StationSearchCriteria.CLOSEST_WITH_BIKE && stationStatus.num_bikes_available > R.string.min_to_replace) {
                result.add(stationStatus.station_id.toInt())
            } else if (criteria == StationSearchCriteria.CLOSEST_WITH_DOCK && stationStatus.num_docks_available > R.string.min_to_replace) {
                result.add(stationStatus.station_id.toInt())
            }
        }

        return result
    }

    fun getStationStatus(stationId: Int, response: String): CitiStationStatus? {
        val model = getStationStatusModel(response)
        for (stationInfo in model.data.stations) {
            if (stationInfo.station_id.toInt() == stationId) {
                return CitiStationStatus(
                    stationInfo.num_bikes_available.toString(),
                    stationInfo.num_ebikes_available.toString(),
                    stationInfo.num_docks_available.toString(),
                    "",
                    stationId,
                    ""
                )
            }
        }
        return null
    }

    private fun getStationStatusModel(string: String): StationStatusModel {
        return json.decodeFromString(string)
    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        return json.decodeFromString(stationInformationContent)
    }


}
