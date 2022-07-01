package com.fan.tiptop.citiapi.location

import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.data.Location
import java.text.DecimalFormat
import kotlin.math.*

enum class UNIT {
    KM, MILE
}

class LocationUtils {
    companion object {
        fun computeAndFormatDistance(
            userLatitude: Double,
            userLongitude: Double,
            stationLatitude: Double,
            stationLongitude: Double,
            unit: UNIT = UNIT.MILE
        ): String {
            val distance =
                computeDistance(
                    userLatitude,
                    userLongitude,
                    stationLatitude,
                    stationLongitude,
                    unit
                )
            val dec = DecimalFormat("#,###.00")
            return "${dec.format(distance)}mi"
        }
        fun getDropShapedClosestStation(
            userLocation: Location?,
            targetStation: CitibikeStationInformationModel,
            listModel: List<CitibikeStationInformationModel>
        ): List<CitibikeStationInformationModelDecorated> {
            if (userLocation==null)
                return listOf()
            var closestStation = listModel.first()
            var minDistance: Double = Double.MAX_VALUE
            for (stationInfo in listModel) {
                val stationDistance = computeDistance(
                    userLocation.latitude,
                    userLocation.longitude,
                    stationInfo.lat,
                    stationInfo.lon
                )
                if (stationDistance < minDistance) {
                    minDistance = stationDistance
                    closestStation = stationInfo
                }
            }
            return listOf(
                CitibikeStationInformationModelDecorated(
                    closestStation,
                    isFavorite = false,
                    distanceRank = 0f
                )
            )
        }
        fun computeDistance(
            userLatitude: Double,
            userLongitude: Double,
            stationLatitude: Double,
            stationLongitude: Double,
            unit: UNIT = UNIT.MILE
        ): Double {
            val userLongitudeRad = Math.toRadians(userLongitude)
            val stationLongitudeRad = Math.toRadians(stationLongitude)
            val userLatitudeRad = Math.toRadians(userLatitude)
            val stationLatitudeRad = Math.toRadians(stationLatitude)

            // Haversine formula
            val dlon: Double = stationLongitudeRad - userLongitudeRad
            val dlat: Double = stationLatitudeRad - userLatitudeRad
            val a = (sin(dlat / 2).pow(2.0)
                    + (cos(userLatitudeRad) * cos(stationLatitudeRad)
                    * sin(dlon / 2).pow(2.0)))

            val c = 2 * asin(sqrt(a))

            // Radius of earth
            val r = when (unit) {
                UNIT.MILE -> {
                    3956.0
                }
                UNIT.KM -> {
                    6371.0
                }
            }
            return c * r
        }
    }


}