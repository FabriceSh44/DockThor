package com.fan.tiptop.citiapi.data

class CitibikeStationInformationModelDecorated(
    val model: CitibikeStationInformationModel,
    val isFavorite: Boolean,
    var distanceRank: Double= Double.MAX_VALUE
) {
}