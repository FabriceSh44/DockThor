package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

@Serializable
data class CitibikeStationModel(
    val num_ebikes_available: Int,
    val station_id: String,
    val is_renting: Int,
    val num_bikes_disabled: Int,
    val station_status: String,
    val is_returning: Int,
    val legacy_id: String,
    val num_docks_disabled: Int,
    val eightd_has_available_keys: Boolean,
    val num_docks_available: Int,
    val is_installed: Int,
    val last_reported: Int,
    val num_bikes_available: Int
)

@Serializable
data class Stations(val stations: List<CitibikeStationModel>)

@Serializable
data class StationStatusModel(val data: Stations, val last_updated: Int, val ttl: Int)

@Serializable
data class RentalUri(val ios: String, val android: String)

@Serializable
data class CitibikeStationInformationModel(
    val lon: Double,
    val external_id: String,
    val eightd_has_key_dispenser: Boolean,
    val short_name: String,
    val name: String,
    val station_type: String,
    val capacity: Int,
    val station_id: String,
    val electric_bike_surcharge_waiver: Boolean,
    val lat: Double,
    val region_id: String,
    val rental_uris: RentalUri,
    val eightd_station_services: List<String>,
    val rental_methods: List<String>,
    val has_kiosk: Boolean,
    val legacy_id: String
)

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
        val model = Json.decodeFromString<StationStatusModel>(string)
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

        return "${station.num_bikes_available}/${station.num_docks_available}"

    }

    fun getStationInformationModel(stationInformationContent: String): StationInformationModel {
        val model = Json.decodeFromString<StationInformationModel>(stationInformationContent)
        return model

    }
}