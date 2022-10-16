package com.fan.tiptop.dockthor.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val TAG: String = "AlarmBroadcastReceiver"

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Log.d(TAG, "Alarm just fired")
    }


}