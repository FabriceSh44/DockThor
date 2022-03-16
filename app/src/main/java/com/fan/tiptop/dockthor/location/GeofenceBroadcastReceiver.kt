package com.fan.tiptop.dockthor.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.fan.tiptop.dockthor.logic.DockThorKernel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "GeofenceBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        val citistationStatus =
            geofencingEvent.triggeringGeofences.firstOrNull()?.requestId?.toInt()
                ?.let { DockThorKernel.getInstance().getCitistationStatus(it) }

        citistationStatus?.let { Log.i(TAG, "has ${it.numDockAvailable} docks") }

        Log.i(TAG, getGeofenceTransitionDetails(geofencingEvent))
    }

    private fun getGeofenceTransitionDetails(event: GeofencingEvent): String {
        val transitionString: String
        val geofenceTransition = event.geofenceTransition
        transitionString = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                "IN"
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                "DWELL"
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                "OUT"
            }
            else -> {
                "OTHER"
            }
        }
        val triggeringIDs: MutableList<String?>
        triggeringIDs = ArrayList()
        for (geofence in event.triggeringGeofences) {
            triggeringIDs.add(geofence.requestId)
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs))
    }
}