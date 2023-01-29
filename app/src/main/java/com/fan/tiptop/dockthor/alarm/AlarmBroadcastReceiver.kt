package com.fan.tiptop.dockthor.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fan.tiptop.citiapi.data.CitiStationId
import com.fan.tiptop.dockthor.logic.DockThorKernel
import java.time.Duration


class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "AlarmBroadcastReceiver"

    companion object {
        fun generateAction(stationId: CitiStationId): String {
            return "com.fan.tiptop.dockthor.START_ALARM.${stationId.value}"
        }
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val stringExtra: String? = intent.getStringExtra("station_id")
        val stationId = if(stringExtra==null) CitiStationId("") else CitiStationId(stringExtra)
        val durationInSec = intent.getLongExtra("duration_in_sec", 0)
        intent.getLongExtra("duration_in_sec", 0)
        Log.i(TAG, "Alarm just fired for ${stationId}")
        DockThorKernel.getInstance()
            .addGeofenceToStation(stationId, Duration.ofSeconds(durationInSec))
        DockThorKernel.getInstance().setupNextAlarmForStation(stationId)
    }
}