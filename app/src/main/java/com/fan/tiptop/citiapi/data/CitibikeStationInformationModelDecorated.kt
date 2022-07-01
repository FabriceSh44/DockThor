package com.fan.tiptop.citiapi.data

class CitibikeStationInformationModelDecorated(
    val model: CitibikeStationInformationModel,
    val isFavorite: Boolean,
    var distanceRank: Float= Float.MAX_VALUE
) {
}