package com.fan.tiptop.dockthor.alarm

import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fan.tiptop.citiapi.data.CitibikeMetaAlarmBean
import com.fan.tiptop.citiapi.database.DockThorDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AlarmManager(val context: AppCompatActivity) {
    private var _alarmManagerAndroid: android.app.AlarmManager? = null
    private val alarmManagerAndroid get() = _alarmManagerAndroid!!

    init {
        _alarmManagerAndroid =
            context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    }

    fun setAlarm(geofenceIntent: PendingIntent, alarmBean: CitibikeMetaAlarmBean) {
        val alarmTime = alarmBean.alarmData.wakeUpTimeInMillis
        // FOR TEST
        // alarmTime = System.currentTimeMillis() + 4000

        CoroutineScope(Dispatchers.IO).launch {
            Log.i(
                TAG,
                "Adding alarm for :${
                    Calendar.getInstance().apply { timeInMillis = alarmTime }.time
                } => timeInMillis:${alarmTime}"
            )
            DockThorDatabase.getInstance(context).dockthorDao.upsert(
                alarmBean.alarmData
            )
            for (alarm in alarmBean.alarms) {
                DockThorDatabase.getInstance(context).dockthorDao.insert(
                    alarm
                )
            }
            alarmManagerAndroid.setExactAndAllowWhileIdle(
                RTC_WAKEUP,
                alarmTime,
                geofenceIntent
            )
        }
    }

    fun removeAlarm(stationId: Int) {
        val intent = Intent()
        intent.action = AlarmBroadcastReceiver.generateAction(stationId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManagerAndroid.cancel(pendingIntent)
    }


    companion object {
        private var _instance: AlarmManager? = null
        private const val TAG = "AlarmManager"

        init {
            Log.i(TAG, "Initializing AlarmManager")
        }

        @Synchronized
        fun getInstance(context: AppCompatActivity): AlarmManager? {
            if (null == _instance) _instance = AlarmManager(context)
            return _instance
        }

        @Synchronized
        fun getInstance(): AlarmManager {
            checkNotNull(_instance) {
                throw Exception("AlarmManager is not initialized, call getInstance(Context) first")
            }
            return _instance as AlarmManager
        }
    }
}
