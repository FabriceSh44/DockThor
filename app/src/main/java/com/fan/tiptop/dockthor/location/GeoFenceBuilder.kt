package com.fan.tiptop.dockthor.location

import android.annotation.SuppressLint
import com.fan.tiptop.citiapi.data.CitiStationId
import com.fan.tiptop.citiapi.data.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

class GeoFenceBuilder {
    companion object {
        private const val GEOFENCE_RADIUS_IN_METERS: Float = 500F
        @SuppressLint("VisibleForTests")
        fun getGeofencingRequest(
            location: Location,
            expirationInSecond: Long,
            geofenceId: CitiStationId
        ): GeofencingRequest {
            val geofenceList = mutableListOf<Geofence>()
            geofenceList.add(
                Geofence.Builder()
                    .setRequestId(geofenceId.value)
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
