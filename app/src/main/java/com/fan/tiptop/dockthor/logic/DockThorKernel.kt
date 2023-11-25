package com.fan.tiptop.dockthor.logic

import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.fan.tiptop.citiapi.CitiKernel
import com.fan.tiptop.citiapi.data.*
import com.fan.tiptop.citiapi.database.DockthorDao
import com.fan.tiptop.dockthor.alarm.AlarmManager
import com.fan.tiptop.dockthor.location.DefaultLocationManagerListener
import com.fan.tiptop.dockthor.location.LocationManager
import com.fan.tiptop.dockthor.network.DefaultNetworkManagerListener
import com.fan.tiptop.dockthor.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import kotlin.time.toKotlinDuration


class DockThorKernel private constructor(val dao: DockthorDao) {

    //CITI
    private val _citiKernel: CitiKernel = CitiKernel()

    //LOG
    private val TAG = "DockThorKernel"
    fun initialize(
        function: (List<CitiStationStatus>, String) -> Unit
    ) {

        NetworkManager.getInstance().stationInformationRequest(
            object : DefaultNetworkManagerListener {
                override fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Default).launch {
                            _citiKernel.processStationInfoRequestResult(result)
                            updateCitistationList(function)
                        }
                    }
                }

                override fun getError(error: String) {
                    if (error.isNotEmpty()) {
                        Log.e(TAG, error)
                        function(listOf(), "Unable to retrieve citibike station information")
                    }
                }
            }
        )

    }

    fun updateCitistationList(onUpdateComplete: (List<CitiStationStatus>, String) -> Unit) {
        LocationManager.getInstance()
            .getLastLocation(object : DefaultLocationManagerListener {
                override fun getLocation(location: android.location.Location?) {
                    CoroutineScope(Dispatchers.Default).launch {
                        val stationInfoModelToDisplay = dao.getFavoriteStations().map { x ->
                            CitibikeStationInformationModelDecorated(
                                x
                            )
                        }.toMutableList()
                        NetworkManager.getInstance()
                            .stationStatusRequest(object : DefaultNetworkManagerListener {
                                override fun getResult(result: String) {
                                    if (result.isNotEmpty()) {
                                        val citiStationStatusToDisplay: List<CitiStationStatus> =
                                            _citiKernel.getCitiStationStatusToDisplay(
                                                result,
                                                stationInfoModelToDisplay,
                                                location.toCitiLocation()
                                            )
                                        onUpdateComplete(citiStationStatusToDisplay, "")
                                    }
                                }

                                override fun getError(error: String) {
                                    Log.e(TAG, error)
                                    onUpdateComplete(
                                        listOf(),
                                        "Unable to retrieve Citibike station status"
                                    )
                                }
                            })
                    }
                }
            })
    }


    fun replaceStationWithCriteria(
        citiStationStatusToReplace: CitiStationStatus,
        criteria: StationSearchCriteria,
        displayedCitistationStatus: List<CitiStationStatus>?,
        onReplaceComplete: (List<CitiStationStatus>, String) -> Unit
    ) {
        LocationManager.getInstance().getLastLocation(object : DefaultLocationManagerListener {
            override fun getLocation(location: android.location.Location?) {
                runBlocking {
                    launch(Dispatchers.Default) {
                        val favoriteStations = dao.getFavoriteStations().map { x ->
                            CitibikeStationInformationModelDecorated(
                                x
                            )
                        }.toMutableList()
                        NetworkManager.getInstance()
                            .stationStatusRequest(object : DefaultNetworkManagerListener {
                                override fun getResult(result: String) {
                                    if (result.isNotEmpty() && location != null) {
                                        val closestCitiStation =
                                            _citiKernel.getClosestStationWithCriteria(
                                                location.toCitiLocation()!!,
                                                citiStationStatusToReplace,
                                                criteria,
                                                result
                                            )
                                        val citiStationStatusToDisplay: MutableList<CitiStationStatus> =
                                            _citiKernel.getCitiStationStatusToDisplay(
                                                result,
                                                favoriteStations, location.toCitiLocation()
                                            )
                                        if (closestCitiStation != null && displayedCitistationStatus != null) {
                                            val index = displayedCitistationStatus.indexOf(
                                                citiStationStatusToReplace
                                            )
                                            if (index != -1) {
                                                citiStationStatusToDisplay.removeAt(index)
                                                citiStationStatusToDisplay.add(
                                                    index,
                                                    closestCitiStation
                                                )
                                            }
                                        }
                                        onReplaceComplete(citiStationStatusToDisplay, "")
                                    }
                                }

                                override fun getError(error: String) {
                                    Log.e(TAG, error)
                                    onReplaceComplete(
                                        listOf(),
                                        "Unable to retrieve Citibike station status"
                                    )
                                }
                            })
                    }
                }
            }
        })
    }

    fun getActionViewIntent(station: CitiStationStatus): Intent? {
        val location: Location =
            _citiKernel.getStationLocation(station.stationId) ?: return null
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
        stationInfoModel.model.isFavorite=station.isFavorite
        dao.insert(stationInfoModel.model)
    }

    suspend fun updateStationName(station: CitiStationStatus) {
        val stationInfoModel = _citiKernel.getCitiInfoModel(station.stationId) ?: return
        stationInfoModel.model.name=station.givenName
        dao.updateName(station.givenName,station.stationId)
    }


    suspend fun removeStationFromFavorite(station: CitiStationStatus) {
        val stationInfoModel = _citiKernel.getCitiInfoModel(station.stationId) ?: return
        stationInfoModel.model.isFavorite=station.isFavorite
        dao.deleteStationsByStationId(listOf(station.stationId))
    }

    fun addGeofenceToStation(stationId: CitiStationId, expiration: Duration) {
        val stationInfoModel =
            _citiKernel.getCitiInfoModel(stationId) ?: return
        LocationManager.getInstance().addGeofence(stationInfoModel.model, expiration)
    }

    fun getCitistationStatus(stationId: CitiStationId, onRequestComplete: (CitiStationStatus?) -> Unit) {
        NetworkManager.getInstance()
            .stationStatusRequest(object : DefaultNetworkManagerListener {
                override fun getResult(result: String) {
                    if (result.isNotEmpty()) {
                        val citiStationStatus: CitiStationStatus? =
                            _citiKernel.getCitiStationStatus(result, stationId)
                        citiStationStatus?.let { it ->
                            _citiKernel.getCitiInfoModel(stationId)?.model?.let { it1 -> it.fillFromStationModel(it1)}
                        }
                        Log.i(TAG, "Get citistationstatus   ${citiStationStatus}")
                        onRequestComplete(citiStationStatus)
                    }
                }

                override fun getError(error: String) {
                    Log.e(TAG, error)
                }
            })

    }


    fun removeAlarmForStation(stationId: CitiStationId) {
        AlarmManager.getInstance().removeAlarm(stationId)
    }

    fun setupNextAlarmForStation(alarmInput: CitibikeMetaAlarmBean) {
        val geofenceIntent = LocationManager.getInstance()
            .getGeofenceIntent(alarmInput)
        AlarmManager.getInstance().setAlarm(geofenceIntent, alarmInput)
    }

    fun setupNextAlarmForStation(stationId: CitiStationId) {
        CoroutineScope(Dispatchers.IO).launch {
            val stationAlarms = dao.getStationAlarms(stationId)
            val stationAlarmData = dao.getStationAlarmData(stationId)
            stationAlarmData?.let { it ->
                setupNextAlarmForStation(
                    CitibikeMetaAlarmBean(
                        stationAlarms,
                        it
                    )
                )
            }
        }
    }


    companion object {
        private var _instance: DockThorKernel? = null

        @Synchronized
        fun getInstance(dao: DockthorDao): DockThorKernel {
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
        return null
    }
    val ageLocation =
        SystemClock.elapsedRealtimeNanos() - this.elapsedRealtimeNanos
    val duration = Duration.ofNanos(ageLocation).toKotlinDuration()
    return Location(this.latitude, this.longitude, duration)
}
