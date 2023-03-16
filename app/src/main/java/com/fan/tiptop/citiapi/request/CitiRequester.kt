package com.fan.tiptop.citiapi.request

import com.fan.tiptop.citiapi.data.*
import com.fan.tiptop.citiapi.location.LocationUtils
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
data class CitibikeStationInformationModels(
    val data: StationInformations,
    val last_updated: Int,
    val ttl: Int
)

class CitiRequester {
    private var _stationIdToStation: MutableMap<CitiStationId, CitibikeStationModel> = mutableMapOf()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getAvailabilitiesWithLocation(
        response: String,
        stationList: List<CitibikeStationInformationModelDecorated>,
        userLocation: Location?
    ): MutableList<CitiStationStatus> {
        val result = mutableListOf<CitiStationStatus>()
        if (stationList.isEmpty()) {
            return result
        }
        val model = getStationStatusModel(response)
        for (stationInfo in model.data.stations) {
            this._stationIdToStation.put(CitiStationId(stationInfo.station_id), stationInfo)
        }
        for (stationInfoModelDecorated in stationList) {
            val stationInfoModel = stationInfoModelDecorated.model
            val stationId = stationInfoModel.station_id
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
            var status = CitiStationStatus(
                numBikeAvailable = stationStatus.num_bikes_available.toString(),
                numEbikeAvailable = stationStatus.num_ebikes_available.toString(),
                numDockAvailable = stationStatus.num_docks_available.toString(),
                distance = distanceDescription,
                isFavorite = stationInfoModelDecorated.model.isFavorite,
            )
            status.fillFromStationModel(stationInfoModel)
            result.add(status)
        }
        return result
    }

    fun getStationStatusWithCriteria(
        response: String,
        criteria: StationSearchCriteria, minToReplace:Int
    ): Map<CitiStationId, CitiStationStatus> {
        val model = getStationStatusModel(response)
        val result = mutableMapOf<CitiStationId, CitiStationStatus>()
        for (stationInfo in model.data.stations) {
            if ((criteria == StationSearchCriteria.CLOSEST_WITH_BIKE && stationInfo.num_bikes_available > minToReplace)
                or (criteria == StationSearchCriteria.CLOSEST_WITH_DOCK && stationInfo.num_docks_available > minToReplace)
            ) {
                result.put(
                    CitiStationId(stationInfo.station_id),
                    CitiStationStatus(
                        stationInfo.num_bikes_available.toString(),
                        stationInfo.num_ebikes_available.toString(),
                        stationInfo.num_docks_available.toString(),
                        "",
                        CitiStationId(stationInfo.station_id),
                        ""
                    )
                )
            }

        }
        return result
    }

    fun getStationStatus(stationId: CitiStationId, response: String): CitiStationStatus? {
        val model = getStationStatusModel(response)
        for (stationInfo in model.data.stations) {
            if (CitiStationId(stationInfo.station_id) == stationId) {
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
    fun getStationInformationModel(stationInformationContent: String): CitibikeStationInformationModels {
        return json.decodeFromString(stationInformationContent)
    }


}
