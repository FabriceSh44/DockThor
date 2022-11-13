package com.fan.tiptop.dockthor.location

import com.fan.tiptop.citiapi.data.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

class GeoFenceBuilder {
    companion object {
        private const val GEOFENCE_RADIUS_IN_METERS: Float = 500F
        fun getGeofencingRequest(
            location: Location,
            expirationInSecond: Long,
            geofenceId: String
        ): GeofencingRequest {
            val geofenceList = mutableListOf<Geofence>()
            geofenceList.add(
                Geofence.Builder()
                    .setRequestId(geofenceId)
                    .setCircularRegion(
                        location.latitude,
                        location.longitude,
                        GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(expirationInSecond * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setLoiteringDelay(1000)//1seconds
                    .build()
            )
            return GeofencingRequest.Builder().apply {
                setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                addGeofences(geofenceList)
            }.build()
        }
    }
}
