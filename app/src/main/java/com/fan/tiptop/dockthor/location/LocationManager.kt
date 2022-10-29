package com.fan.tiptop.dockthor.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel
import com.fan.tiptop.dockthor.alarm.AlarmBroadcastReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.time.Duration

class LocationManager private constructor(val context: AppCompatActivity) {
    @SuppressLint("MissingPermission")
    fun getLastLocation(listener: DefaultLocationManagerListener) {
        if (!_hasLocationPermission) {
            listener.getLocation(null)
        }
        _fusedLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        )
            .addOnSuccessListener { location: Location? ->
               listener.getLocation(location)
            }
            .addOnFailureListener {
                listener.getLocation(null)
            }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(
        station: CitibikeStationInformationModel,
        expiration: Duration,
    ) {
        val location: com.fan.tiptop.citiapi.data.Location =
            com.fan.tiptop.citiapi.data.Location(station.lat, station.lon)
        val geofencingRequest = GeoFenceBuilder.getGeofencingRequest(
            location, expiration.seconds, station.station_id
        )

        if (_hasLocationPermission) {
            _geofencingClient.addGeofences(geofencingRequest, getGeofenceCreationIntent(station.station_id.toInt())).run {
                addOnSuccessListener {
                    Log.i(
                        TAG,
                        "Successfully add geofence for station id ${station.station_id}"
                    )
                }
                addOnFailureListener {
                    Log.e(
                        TAG,
                        "Failed to add geofence for station id ${station.station_id}"
                    )
                }
            }
        }
    }

    private fun fillLocationPermission(context: AppCompatActivity) {
        if (!hasPermission(context)) {
            val requestPermissionLauncher = context.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                _hasLocationPermission = true
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                    }
                    else -> {
                        // No location access granted.
                        _hasLocationPermission = false
                    }
                }

            }
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            _hasLocationPermission = true
        }
    }

    private fun hasPermission(context: AppCompatActivity) =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun getGeofenceIntent(station: CitiStationStatus,expiration:Duration): PendingIntent {
        val action = AlarmBroadcastReceiver.generateAction(station.stationId)
        context.registerReceiver(alarmBroadcastReceiver, IntentFilter(action))
        Intent().also { intent ->
            intent.action = action
            intent.putExtra("station_id", station.stationId)
            intent.putExtra("duration_in_sec", expiration.seconds)
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }
    private fun getGeofenceCreationIntent(stationId:Int): PendingIntent {
        val action = "com.fan.tiptop.dockthor.CREATE_GEOFENCE.${stationId}"
        context.registerReceiver(geofenceBroadcastReceiver, IntentFilter(action))
        Intent().also { intent ->
            intent.action = action
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)// MUTABLE is mandatory for geofencing
        }
    }


    companion object {
        private const val TAG = "LocationManager"
        private var _instance: LocationManager? = null

        @Synchronized
        fun getInstance(context: AppCompatActivity): LocationManager? {
            if (null == _instance) _instance = LocationManager(context)
            return _instance
        }

        @Synchronized
        fun getInstance(): LocationManager {
            checkNotNull(_instance) {
                throw Exception("LocationManager is not initialized, call getInstance(Context) first")
            }
            return _instance as LocationManager
        }
    }

    private var _geofencingClient: GeofencingClient
    private var _hasLocationPermission: Boolean = false
    private var _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    private val alarmBroadcastReceiver = AlarmBroadcastReceiver()
    private val geofenceBroadcastReceiver = GeofenceBroadcastReceiver()

    init {
        Log.i(TAG,"Initializing LocationManager")
        fillLocationPermission(context)
        _geofencingClient = LocationServices.getGeofencingClient(context)

        Log.i(TAG,"Register broadcast receiver")

    }




}
