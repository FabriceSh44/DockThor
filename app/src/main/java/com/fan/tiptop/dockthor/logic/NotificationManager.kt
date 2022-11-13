package com.fan.tiptop.dockthor.logic

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.dockthor.R
import android.app.NotificationManager as AndroidNotificationManager

class NotificationManager private constructor(private val context: Context) {

    private val CHANNEL_ID: String = "0"
    private val TAG: String="NotificationManager"
    init {
        Log.i(Companion.TAG, "Initializing NotificationManager")
        createNotificationChannel()
    }


    fun sendNotification(citiStationStatus: CitiStationStatus) {
        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_dock_24)
            .setContentTitle("Dock alert")
            .setContentText("${citiStationStatus.address}: ${citiStationStatus.numDockAvailable} docks available")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "com.fan.tiptop.dockthor.notification"
            val descriptionText = "com.fan.tiptop.dockthor.notification_description"
            val importance = AndroidNotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: AndroidNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    companion object {
        private var _instance: NotificationManager? = null
        private const val TAG = "NotificationManager"

        init {
            Log.i(TAG, "Initializing NotificationManager")
        }

        @Synchronized
        fun getInstance(context: AppCompatActivity): NotificationManager? {
            if (null == _instance) _instance = NotificationManager(context)
            return _instance
        }

        @Synchronized
        fun getInstance(): NotificationManager {
            checkNotNull(_instance) {
                throw Exception("NotificationManager is not initialized, call getInstance(Context) first")
            }
            return _instance as NotificationManager
        }
    }


}
