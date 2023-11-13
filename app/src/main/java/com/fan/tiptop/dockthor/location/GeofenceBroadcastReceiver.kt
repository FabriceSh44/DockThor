package com.fan.tiptop.dockthor.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.fan.tiptop.citiapi.data.CitiStationId
import com.fan.tiptop.dockthor.logic.DockThorKernel
import com.fan.tiptop.dockthor.logic.NotificationManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "GeofenceBroadcastReceiver"

    @SuppressLint("VisibleForTests")
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive context:${context} intent:${intent} ")

        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null)
        {
            Log.e(TAG, "received null geofencing event")
            return
        }
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        Log.i(TAG, "received geofencing event:${geofencingEvent}. Retrieving citistation status")
        Log.i(TAG, getGeofenceTransitionDetails(geofencingEvent))
        //can be null despite what AS is saying
        val triggeringGeofences = geofencingEvent.triggeringGeofences
        if (triggeringGeofences == null) {
            geofencingEvent.triggeringLocation
            Log.i(TAG, "No geofence triggered")
            return
        }
        triggeringGeofences.first().requestId
            .let {
                DockThorKernel.getInstance().getCitistationStatus(CitiStationId(it)) { it2 ->
                    it2?.let {
                        Log.i(TAG, "${it.address} has ${it.numDockAvailable} docks")
                        if (it.numDockAvailable.toInt() < 5) {
                            NotificationManager.getInstance().sendNotification(it)
                        }
                    }
                }
            }
    }

    @SuppressLint("VisibleForTests")
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
        val triggeringGeofences = event.triggeringGeofences
        if (triggeringGeofences != null) {
            for (geofence in triggeringGeofences) {
                triggeringIDs.add(geofence.requestId)
            }
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs))
    }
}