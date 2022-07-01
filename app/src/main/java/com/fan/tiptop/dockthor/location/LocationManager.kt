package com.fan.tiptop.dockthor.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LocationManager private constructor(val context: AppCompatActivity) {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(listener: DefaultLocationManagerListener) {
        if (!_hasLocationPermission) {
            listener.getLocation(null)
        }
        _fusedLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        )
            .addOnSuccessListener { location: Location? ->
                runBlocking { launch { listener.getLocation(location) } }
            }
            .addOnFailureListener {
                runBlocking { launch { listener.getLocation(null) } }
            }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(
        station: CitibikeStationInformationModel,
        expirationInSecond: Long,
    ) {
        val location: com.fan.tiptop.citiapi.data.Location =
            com.fan.tiptop.citiapi.data.Location(station.lat, station.lon)
        val geofencingRequest = GeoFenceBuilder.getGeofencingRequest(
            location, expirationInSecond, station.station_id
        )

        if (_hasLocationPermission) {
            _geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
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

    init {
        fillLocationPermission(context)
        _geofencingClient = LocationServices.getGeofencingClient(context)
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
