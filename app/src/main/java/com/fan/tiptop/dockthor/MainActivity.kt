package com.fan.tiptop.dockthor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fan.tiptop.citiapi.CitiRequestor

class MainActivity : AppCompatActivity() {
    private val TAG = "DockThor"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bikeAtStationButton = findViewById<Button>(R.id.bike_at_station_button)
        bikeAtStationButton.setOnClickListener {
            onBikeAtStationClick()
        }
    }


    fun onBikeAtStationClick() {
        val bikeAtStationButton = findViewById<Button>(R.id.bike_at_station_button)
        val queue = Volley.newRequestQueue(this)
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json"
        Log.v(TAG, "Requesting $url");
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                bikeAtStationButton.text = processResponse(response)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Unable to request. Got error ${error}")
                bikeAtStationButton.text = "That didn't work!"


            })
        queue.add(stringRequest)
    }

    fun processResponse(response: String): String {
        try {
            val requestor = CitiRequestor()
            val availabilities = requestor.getAvailabilities(response, 4620)
            return "$availabilities"
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return "NaN"
        }
    }
}