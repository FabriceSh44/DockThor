package com.fan.tiptop.citiapi.data

class CitibikeStationInformationModelDecorated(
    val model: CitibikeStationInformationModel,
    var isClosest: Boolean,
    val isFavorite: Boolean
) {
}