package com.fan.tiptop.citiapi.data

import kotlinx.serialization.Serializable

@Serializable //to be en/decoded in json
data class CitibikeStationInformationModel(
    val station_id: String,
    val name: String,
    val capacity: Int,
    val lon: Double,
    val lat: Double,
)

