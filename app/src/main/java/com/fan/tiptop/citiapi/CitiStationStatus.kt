package com.fan.tiptop.citiapi

import java.time.LocalDateTime

data class CitiStationStatus(
    val numBikeAvailable: Int,
    val numEbikeAvailable: Int,
    val numDockAvailable: Int,
    val lastUpdatedTime: LocalDateTime,
    val address: String
)
