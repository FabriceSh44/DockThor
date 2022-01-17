package com.fan.tiptop.dockthor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
    fun isConnected():Boolean{
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    fun onBikeAtStationClick() {
        val bikeAtStationButton = findViewById<Button>(R.id.bike_at_station_button)
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json"
        Log.v(TAG, "Requesting $url");
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                bikeAtStationButton.text =processResponse(response)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Unable to request. Got error ${error}")
                bikeAtStationButton.text = "That didn't work!"


            })
        queue.add(stringRequest)
    }
    fun processResponse(response:String):String{
        try {
            val requestor = CitiRequestor()
            val availibility = requestor.getAvailabilities(response,4620)
            return "$availibility"
        }
        catch(e:Exception)
        {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return "NaN"
        }
    }
}