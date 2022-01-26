package com.fan.tiptop.dockthor.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

class NetworkManager private constructor(context: Context) {
    //for Volley API
    var requestQueue: RequestQueue
    fun stationStatusRequest( listener: SomeCustomListener) {
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json"
        val request = StringRequest(
            Request.Method.GET, url,
        Response.Listener { response ->
            Log.d(TAG + ": ", "somePostRequest Response : $response")
            if (null != response) listener.getResult(response.toString())
        },
        Response.ErrorListener { error ->
            if (null != error.networkResponse) {
                Log.e(TAG, "Error with request. Error: ${error}" )
                listener.getError(error.toString())
            }

        })
        requestQueue.add(request)
    }
    fun stationInformationRequest( listener: SomeCustomListener) {
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_information.json"
        val request = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                Log.d(TAG + ": ", "somePostRequest Response : $response")
                if (null != response) listener.getResult(response.toString())
            },
            Response.ErrorListener { error ->
                if (null != error.networkResponse) {
                    Log.e(TAG, "Error with request. Error: ${error}" )
                    listener.getError(error.toString())
                }

            })
        requestQueue.add(request)
    }

    companion object {
        private const val TAG = "NetworkManager"
        private var instance: NetworkManager? = null
        private const val prefixURL = "http://some/url/prefix/"
        @Synchronized
        fun getInstance(context: Context): NetworkManager? {
            if (null == instance) instance = NetworkManager(context)
            return instance
        }

        //this is so you don't need to pass context each time
        @Synchronized
        fun getInstance(): NetworkManager {
            checkNotNull(instance) {
                throw Exception("NetworkManage is not initialized, call getInstance(Context) first")
            }
            return instance as NetworkManager
        }
    }

    init {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext())
        //other stuf if you need
    }
}
