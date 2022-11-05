package com.fan.tiptop.dockthor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fan.tiptop.dockthor.alarm.AlarmManager
import com.fan.tiptop.dockthor.location.LocationManager
import com.fan.tiptop.dockthor.logic.NotificationManager
import com.fan.tiptop.dockthor.network.NetworkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkManager.getInstance(this)
        LocationManager.getInstance(this)
        AlarmManager.getInstance(this)
        NotificationManager.getInstance(this)
        setContentView(R.layout.activity_main)
    }
}
