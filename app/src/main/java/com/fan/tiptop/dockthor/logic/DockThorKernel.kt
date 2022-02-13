package com.fan.tiptop.dockthor.logic

import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.fan.tiptop.citiapi.CitiKernel
import com.fan.tiptop.citiapi.Location
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.database.CitibikeStationInformationDao
import com.fan.tiptop.dockthor.location.DefaultLocationManagerListener
import com.fan.tiptop.dockthor.location.LocationManager
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import java.time.Duration
import java.util.*
import kotlin.time.toKotlinDuration


class DockThorKernel(val dao: CitibikeStationInformationDao) {

    //CITI
    private val _citiKernel: CitiKernel = CitiKernel()

    //LOG
    private val TAG = "DockThorKernel"
    suspend fun initialize(function: (List<CitiStationStatus>, String) -> Unit) {
        NetworkManager.getInstance().stationInformationRequest(
            object : DefaultNetworkManagerListener {
                override suspend fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        _citiKernel.processStationInfoRequestResult(result)
                        updateCitistationList(function)
                    }
                }

                override suspend fun getError(error: String) {
                    if (error.isNotEmpty()) {
                        Log.e(TAG, error)
                    }
                }
            }
        )
    }

    suspend fun updateCitistationList(onUpdateComplete: (List<CitiStationStatus>, String) -> Unit) {
        LocationManager.getInstance().getLastLocation(object : DefaultLocationManagerListener {
            override suspend fun getLocation(location: android.location.Location?) {
                val stationInfoModelToDisplay = dao.getFavoriteStations().map { x ->
                    CitibikeStationInformationModelDecorated(
                        x,
                        isClosest = false,
                        isFavorite = true
                    )
                }.toMutableList()
                NetworkManager.getInstance()
                    .stationStatusRequest(object : DefaultNetworkManagerListener {
                        override suspend fun getResult(result: String) {
                            if (result.isNotEmpty()) {
                                val citiStationStatusToDisplay: List<CitiStationStatus> =
                                    _citiKernel.getCitiStationStatusToDisplay(
                                        result,
                                        stationInfoModelToDisplay, location.toCitiLocation()
                                    )
                                onUpdateComplete(citiStationStatusToDisplay, "")
                            }
                        }

                        override suspend fun getError(error: String) {
                            Log.e(TAG, error)
                            onUpdateComplete(listOf(), "Unable to retrieve Citibike station status")
                        }
                    })
            }
        })
    }

    fun getActionViewIntent(station: CitiStationStatus) :Intent?{
        val location: Location = _citiKernel.getStationLocation(station.stationId) ?: return null
        val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", location.latitude, location.longitude)
        return Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    }

}

private fun android.location.Location?.toCitiLocation(): Location? {
    if (this == null) {
        return null;
    }
    val ageLocation =
        SystemClock.elapsedRealtimeNanos() - this.elapsedRealtimeNanos
    val duration = Duration.ofNanos(ageLocation).toKotlinDuration()
    return Location(this.latitude, this.longitude, duration)
}
