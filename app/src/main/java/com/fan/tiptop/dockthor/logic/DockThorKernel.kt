package com.fan.tiptop.dockthor.logic

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.fan.tiptop.citiapi.CitiKernel
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.data.Location
import com.fan.tiptop.citiapi.data.StationSearchCriteria
import com.fan.tiptop.citiapi.database.CitibikeStationInformationDao
import com.fan.tiptop.dockthor.alarm.AlarmInput
import com.fan.tiptop.dockthor.alarm.AlarmManager
import com.fan.tiptop.dockthor.location.DefaultLocationManagerListener
import com.fan.tiptop.dockthor.location.LocationManager
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import java.time.Duration
import kotlin.time.toKotlinDuration


class DockThorKernel private constructor(val dao: CitibikeStationInformationDao) {

    private val setGeofenceIntent: PendingIntent? = null

    //CITI
    private val _citiKernel: CitiKernel = CitiKernel()

    //LOG
    private val TAG = "DockThorKernel"
    fun initialize(function: (List<CitiStationStatus>, String) -> Unit) {
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

    suspend fun replaceStationWithCriteria(
        citiStationStatusToReplace: CitiStationStatus,
        criteria: StationSearchCriteria,
        onReplaceComplete: (List<CitiStationStatus>, String) -> Unit
    ) {
        LocationManager.getInstance().getLastLocation(object : DefaultLocationManagerListener {
            override suspend fun getLocation(location: android.location.Location?) {
                val favoriteStations = dao.getFavoriteStations().map { x ->
                    CitibikeStationInformationModelDecorated(
                        x,
                        isFavorite = true
                    )
                }.toMutableList()
                NetworkManager.getInstance()
                    .stationStatusRequest(object : DefaultNetworkManagerListener {
                        override suspend fun getResult(result: String) {
                            if (result.isNotEmpty() && location != null) {
                                val closestCitiStation = _citiKernel.getClosestStationWithCriteria(
                                    location.toCitiLocation()!!,
                                    citiStationStatusToReplace,
                                    criteria,
                                    result, MIN_TO_REPLACE
                                )
                                val citiStationStatusToDisplay: MutableList<CitiStationStatus> =
                                    _citiKernel.getCitiStationStatusToDisplay(
                                        result,
                                        favoriteStations, location.toCitiLocation()
                                    )
                                if (closestCitiStation != null) {
                                    val index = citiStationStatusToDisplay.indexOf(
                                        citiStationStatusToReplace
                                    )
                                    if (index != -1) {
                                        citiStationStatusToDisplay.removeAt(index)
                                        citiStationStatusToDisplay.add(index, closestCitiStation)
                                    }
                                }
                                onReplaceComplete(citiStationStatusToDisplay, "")
                            }
                        }

                        override suspend fun getError(error: String) {
                            Log.e(TAG, error)
                            onReplaceComplete(
                                listOf(),
                                "Unable to retrieve Citibike station status"
                            )
                        }
                    })
            }
        })
    }

    fun getActionViewIntent(station: CitiStationStatus): Intent? {
        val location: Location = _citiKernel.getStationLocation(station.stationId) ?: return null
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("www.google.com")
            .appendPath("maps")
            .appendPath("dir")
            .appendPath("")
            .appendQueryParameter("api", "1")
            .appendQueryParameter(
                "destination",
                location.latitude.toString() + "," + location.longitude
            )
        return Intent(Intent.ACTION_VIEW, Uri.parse(builder.build().toString()))
    }

    suspend fun addStationToFavorite(station: CitiStationStatus) {
        val stationInfoModel = _citiKernel.getCitiInfoModel(station.stationId) ?: return
        dao.insert(stationInfoModel.model)
    }

    suspend fun removeStationFromFavorite(station: CitiStationStatus) {
        val stationInfoModel = _citiKernel.getCitiInfoModel(station.stationId) ?: return
        dao.delete(stationInfoModel.model)
    }

    fun addGeofenceToStation(citiStationStatus: CitiStationStatus) {
        val stationInfoModel = _citiKernel.getCitiInfoModel(citiStationStatus.stationId) ?: return
        LocationManager.getInstance().addGeofence(stationInfoModel.model, 3600)
    }

    fun getCitistationStatus(stationId: Int): CitiStationStatus? {
        NetworkManager.getInstance().stationStatusRequest(object : DefaultNetworkManagerListener {
            override suspend fun getResult(result: String) {
                if (result.isNotEmpty()) {
                    val citiStationStatus: CitiStationStatus? =
                        _citiKernel.getCitiStationStatus(result, stationId)
                    Log.i(TAG, "Get citistationstatus   ${citiStationStatus}")
                }
            }

            override suspend fun getError(error: String) {
                Log.e(TAG, error)
            }
        })

        return null
    }


    fun removeAlarmForStation(station: CitiStationStatus, alarmInputs: List<AlarmInput>) {
        print(station)
        for (alarmInput in alarmInputs) {
            AlarmManager.getInstance().removeAlarm(alarmInput)
        }
    }

    fun addAlarmForStation(station: CitiStationStatus, alarmInputs: List<AlarmInput>) {
        print(station)
        for (alarmInput in alarmInputs) {
            if (setGeofenceIntent != null) {
                AlarmManager.getInstance().setAlarm(setGeofenceIntent, alarmInput)
            }
        }
    }


    companion object {
        val MIN_TO_REPLACE: Int = 2
        private var _instance: DockThorKernel? = null

        @Synchronized
        fun getInstance(dao: CitibikeStationInformationDao): DockThorKernel {
            if (null == _instance) _instance = DockThorKernel(dao)
            return _instance!!
        }

        @Synchronized
        fun getInstance(): DockThorKernel {
            checkNotNull(_instance) {
                throw Exception("DockThorKernel is not initialized, call getInstance(dao) first")
            }
            return _instance as DockThorKernel
        }
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
