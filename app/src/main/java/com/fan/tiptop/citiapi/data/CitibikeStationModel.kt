package com.fan.tiptop.citiapi.data

import kotlinx.serialization.Serializable

@Serializable
data class CitibikeStationModel(
    val num_ebikes_available: Int,
    val station_id: String,
    val is_renting: Int,
    val num_bikes_disabled: Int,
    val is_returning: Int,
    val legacy_id: String,
    val num_docks_disabled: Int,
    val eightd_has_available_keys: Boolean,
    val num_docks_available: Int,
    val is_installed: Int,
    val last_reported: Int,
    val num_bikes_available: Int
)