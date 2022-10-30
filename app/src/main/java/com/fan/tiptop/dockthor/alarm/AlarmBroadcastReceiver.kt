package com.fan.tiptop.dockthor.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fan.tiptop.dockthor.logic.DockThorKernel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration


class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "AlarmBroadcastReceiver"

    companion object {
        fun generateAction(stationId: Int): String {
            return "com.fan.tiptop.dockthor.START_ALARM.${stationId}"
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val stationId = intent.getIntExtra("station_id", 0)
        val durationInSec = intent.getLongExtra("duration_in_sec", 0)
        val dockThreshold = intent.getLongExtra("duration_in_sec", 0)
        Log.i(TAG, "Alarm just fired for ${stationId}")
        DockThorKernel.getInstance()
            .addGeofenceToStation(stationId, Duration.ofSeconds(durationInSec))
    }
}