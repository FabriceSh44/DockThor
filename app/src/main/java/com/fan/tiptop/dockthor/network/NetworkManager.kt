package com.fan.tiptop.dockthor.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class NetworkManager private constructor(context: Context) {
    init {
        Log.i(TAG, "Initializing Network manager")
    }

    //for Volley API
    private var requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)
    fun stationStatusRequest(listener: DefaultNetworkManagerListener) {
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_status.json"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d(TAG, "somePostRequest Response : $response")
                if (null != response) {
                    listener.getResult(response.toString())
                }
            },
            { error ->
                Log.e(TAG, "Error with request. Error: ${error}")
                listener.getError(error?.message ?: "")
            })
        requestQueue.add(request)
    }

    fun stationInformationRequest(listener: DefaultNetworkManagerListener) {
        val url = "https://gbfs.citibikenyc.com/gbfs/en/station_information.json"
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d(TAG, "somePostRequest Response : $response")
                if (null != response) {
                    listener.getResult(response.toString())
                }
            },
            { error ->
                Log.e(TAG, "Error with request. Error: ${error}")
                listener.getError(error?.message ?: "")
            })
        requestQueue.add(request)
    }

    companion object {
        private const val TAG = "NetworkManager"
        private var instance: NetworkManager? = null

        @Synchronized
        fun getInstance(context: Context): NetworkManager? {
            if (null == instance) instance = NetworkManager(context)
            return instance
        }

        @Synchronized
        fun getInstance(): NetworkManager {
            checkNotNull(instance) {
                throw Exception("NetworkManager is not initialized, call getInstance(Context) first")
            }
            return instance as NetworkManager
        }
    }

}
