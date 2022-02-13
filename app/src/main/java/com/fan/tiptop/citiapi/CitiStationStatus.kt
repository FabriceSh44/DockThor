package com.fan.tiptop.citiapi

data class CitiStationStatus(
    val numBikeAvailable: String,
    val numEbikeAvailable: String,
    val numDockAvailable: String,
    val address: String,
    val stationId: Int,
    var distance:String?=null,
    var selected: Boolean=false
)
