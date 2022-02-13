package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.DecimalFormat
import kotlin.math.*

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

    fun getAvailabilitiesWithLocation(
        response: String,
        stationList: List<CitibikeStationInformationModel>,
        userLatitude: Double?,
        userLongitude: Double?
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
            var distanceDescription = ""
            if (userLatitude != null && userLongitude != null) {
                val distance = computeDistance(
                    userLatitude,
                    userLongitude,
                    stationInfoModel.lat,
                    stationInfoModel.lon
                )
                val dec = DecimalFormat("#,###.00")
                distanceDescription="${dec.format(distance)}mi"
            }
            result.add(
                CitiStationStatus(
                    stationStatus.num_bikes_available.toString(),
                    stationStatus.num_ebikes_available.toString(),
                    stationStatus.num_docks_available.toString(),
                    stationInfoModel.name,
                    stationInfoModel.station_id.toInt(),
                    distanceDescription
                    )
            )
        }
        return result
    }

    private fun computeDistance(
        userLatitude: Double,
        userLongitude: Double,
        stationLatitude: Double,
        stationLongitude: Double
    ): Double {
        val userLongitudeRad = Math.toRadians(userLongitude)
        val stationLongitudeRad = Math.toRadians(stationLongitude)
        val userLatitudeRad = Math.toRadians(userLatitude)
        val stationLatitudeRad = Math.toRadians(stationLatitude)

        // Haversine formula
        val dlon: Double = stationLongitudeRad - userLongitudeRad
        val dlat: Double = stationLatitudeRad - userLatitudeRad
        val a = (sin(dlat / 2).pow(2.0)
                + (cos(userLatitudeRad) * cos(stationLatitudeRad)
                * sin(dlon / 2).pow(2.0)))

        val c = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 6371.0
        // for km
        val r = 3956

        // calculate the result
        return c * r
    }


    private fun getStationStatusModel(string: String): StationStatusModel {
        return json.decodeFromString(string)
    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        return json.decodeFromString(stationInformationContent)
    }
}
