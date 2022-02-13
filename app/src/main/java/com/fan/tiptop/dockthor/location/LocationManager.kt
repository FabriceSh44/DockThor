package com.fan.tiptop.dockthor.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LocationManager private constructor(context: AppCompatActivity) {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(listener: DefaultLocationManagerListener) {
        if (!_hasLocationPermission) {
            listener.getLocation(null)
        }
        _fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                runBlocking {
                    launch {
                        listener.getLocation(location)
                    }
                }
            }
            .addOnFailureListener {
                runBlocking {
                    launch {
                        listener.getLocation(null)
                    }
                }
            }
    }


    private fun fillLocationPermission(context: AppCompatActivity) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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

    private var _hasLocationPermission: Boolean = false
    private var _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    init {
        fillLocationPermission(context)
    }
}
