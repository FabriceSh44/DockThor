package com.fan.tiptop.citiapi.location

import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.data.Location
import com.fan.tiptop.dockthor.R
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

        private fun computeDistance(
            location1: Location,
            location2: Location,
            unit: UNIT = UNIT.MILE
        ): Double {
            return computeDistance(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude,
                unit
            )
        }

        fun computeDistance(
            location1Latitude: Double,
            location1Longitude: Double,
            location2Latitude: Double,
            location2Longitude: Double,
            unit: UNIT = UNIT.MILE
        ): Double {
            val userLongitudeRad = Math.toRadians(location1Longitude)
            val stationLongitudeRad = Math.toRadians(location2Longitude)
            val userLatitudeRad = Math.toRadians(location1Latitude)
            val stationLatitudeRad = Math.toRadians(location2Latitude)

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

        fun getDropShapedClosestStation(
            candidateModel: Collection<CitibikeStationInformationModelDecorated>,
            userLocation: Location,
            targetStationLocation: Location
        ): List<CitibikeStationInformationModelDecorated> {
            val result = mutableListOf<Pair<Double, CitibikeStationInformationModelDecorated>>()
            for (model in candidateModel) {
                val distance = computeVShapeDistance(
                    Location(model.model.lat, model.model.lon),
                    userLocation,
                    targetStationLocation
                )
                result.add(Pair(distance, model))
            }
            result.sortBy { it.first }
            return result.map { it.second }
        }

        private fun computeVShapeDistance(
            candidateStationLocation: Location,
            userLocation: Location,
            targetStationLocation: Location
        ): Double {
            return computeDistance(
                userLocation,
                candidateStationLocation
            ) * R.string.bike_factor + computeDistance(
                candidateStationLocation,
                targetStationLocation
            )
        }

    }


}