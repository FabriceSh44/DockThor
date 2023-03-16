package com.fan.tiptop.citiapi.data

class CitibikeStationInformationModelDecorated(
    val model: StationInformationModel,
    var distanceRank: Double= Double.MAX_VALUE
) {
}