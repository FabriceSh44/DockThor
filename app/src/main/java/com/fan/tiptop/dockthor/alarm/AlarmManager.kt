package com.fan.tiptop.dockthor.alarm

import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fan.tiptop.dockthor.location.LocationManager
import java.util.*

class AlarmInput(
    val dayOfWeek: Int,
    val hourOfDay: Int,
    val minuteOfDay: Int
)

class AlarmManager(val context: AppCompatActivity) {
    private var _alarmManagerAndroid: android.app.AlarmManager? = null
    private val alarmManagerAndroid get() = _alarmManagerAndroid!!

    init {
        _alarmManagerAndroid =
            context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    }

    fun setAlarm(geofenceIntent: PendingIntent, alarmInput: AlarmInput) {
        val nowCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val alarmCalendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmInput.hourOfDay)
            set(Calendar.MINUTE, alarmInput.minuteOfDay)
            set(Calendar.SECOND, 0)
        }
        alarmCalendar.apply { set(Calendar.DAY_OF_WEEK, alarmInput.dayOfWeek) }

        if (alarmCalendar < nowCalendar) {
            alarmCalendar.apply {
                set(
                    Calendar.DAY_OF_YEAR,
                    alarmCalendar.get(Calendar.DAY_OF_YEAR) + 7
                )
            }
        }

        Log.i(
            TAG,
            "Adding alarm for :${alarmCalendar.time} => timeInMillis:${alarmCalendar.timeInMillis}"
        )

        alarmManagerAndroid.setExactAndAllowWhileIdle(
            RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            geofenceIntent
        )
    }

    fun removeAlarm(alarmInput: AlarmInput) {
//        TODO("Not yet implemented")
    }

    companion object {
        private var _instance: AlarmManager? = null
        private const val TAG = "AlarmManager"

        init{
            Log.i(TAG,"Initializing AlarmManager")
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
