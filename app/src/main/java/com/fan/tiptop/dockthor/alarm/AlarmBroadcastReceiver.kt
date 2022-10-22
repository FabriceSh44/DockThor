package com.fan.tiptop.dockthor.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fan.tiptop.dockthor.logic.DockThorKernel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "AlarmBroadcastReceiver"

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        runBlocking{
            launch{
                val stationId = intent.getIntExtra("station_id",0)
                Log.i(TAG, "Alarm just fired for ${stationId}")
                DockThorKernel.getInstance().addGeofenceToStation(stationId)
            }}
    }
}