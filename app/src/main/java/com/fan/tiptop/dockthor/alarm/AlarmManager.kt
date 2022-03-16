package com.fan.tiptop.dockthor.alarm

import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.time.DayOfWeek
import java.time.Duration
import java.util.*

class AlarmInput(val dayOfWeek: DayOfWeek, val hourOfDay: Int, val minuteOfDay: Int, val geofenceElapsedTime: Duration) {
}

class AlarmManager(val context: AppCompatActivity) {
    private var _alarmManagerAndroid: android.app.AlarmManager? = null

    init {
        _alarmManagerAndroid =
            context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    }

    fun setAlarm(geofenceIntent: PendingIntent, alarmInput: AlarmInput) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, alarmInput.dayOfWeek.value)
            set(Calendar.HOUR_OF_DAY, alarmInput.hourOfDay)
            set(Calendar.MINUTE, alarmInput.minuteOfDay)
        }
        Log.i(
            AlarmManager.TAG,
            "Adding alarm for day:${alarmInput.dayOfWeek} hourOfDay:${alarmInput.hourOfDay} minuteOfDay:${alarmInput.minuteOfDay} => timeInMillis:${calendar.timeInMillis}"
        )
        _alarmManagerAndroid?.setExactAndAllowWhileIdle(
            RTC_WAKEUP,
            calendar.timeInMillis,
            geofenceIntent
        )
    }

    companion object {
        private var _instance: AlarmManager? = null
        private const val TAG = "AlarmManager"

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