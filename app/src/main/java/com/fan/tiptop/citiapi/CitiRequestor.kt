package com.fan.tiptop.citiapi

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
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
data class Data(val data: Stations, val last_updated: Int, val ttl: Int)

class CitiRequestor {
    private var m_stationIdToStation: MutableMap<Int, CitibikeStationModel> = mutableMapOf()
    fun loadString(string: String): Data {
        val model = Json.decodeFromString<Data>(string)
        return model
    }

    fun load(inputStream: InputStream): Data {
        val model = Json.decodeFromStream<Data>(inputStream)
        return model
    }

    fun getAvailabilities(response: String, stationId: Int): String {
        var station = this.m_stationIdToStation[stationId]
        if (station == null) {
            val model = loadString(response)
            for (station in model.data.stations) {
                this.m_stationIdToStation.put(station.station_id.toInt(), station)
            }
            station =this.m_stationIdToStation[stationId]
            if (station == null) {
                throw Exception("Unable to find station for $stationId")
            }
        }

        return "${station.num_bikes_available}/${station.num_docks_available}"

    }
}