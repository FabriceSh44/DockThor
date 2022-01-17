package com.fan.tiptop.dockthor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fan.tiptop.dockthor.network.NetworkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetworkManager.getInstance(this);
    }


}