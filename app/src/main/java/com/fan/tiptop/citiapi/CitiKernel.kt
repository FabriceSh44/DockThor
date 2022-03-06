package com.fan.tiptop.citiapi

import android.util.Log
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.location.LocationUtils
import kotlin.time.Duration

class CitiKernel {

    //LOG
    private val TAG = "CitiKernel"

    //CITI
    private val _requester = CitiRequester()
    private var _stationInformationModelMap: Map<Int, CitibikeStationInformationModelDecorated> =
        mapOf()

    fun processStationInfoRequestResult(result: String) {
        try {
            _stationInformationModelMap =
                _requester.getStationInformationModel(result).data.stations.map { x ->
                    x.station_id.toInt() to
                            CitibikeStationInformationModelDecorated(
                                x,
                                isClosest = false,
                                isFavorite = false
                            )
                }.toMap()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
        }
    }

    fun getCitiStationStatusToDisplay(
        result: String,
        stationInfoModelToDisplay: MutableList<CitibikeStationInformationModelDecorated>,
        userLocation: Location?
    ): List<CitiStationStatus> {
        if (userLocation != null) {
            val closestStation = getClosestStation(userLocation)
            updateOrAddClosestStation(stationInfoModelToDisplay, closestStation)
        }
        try {
            if (stationInfoModelToDisplay.isNotEmpty()) {
                return _requester.getAvailabilitiesWithLocation(
                    result, stationInfoModelToDisplay,
                    userLocation
                )
            }
            return listOf()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
            return listOf()
        }
    }

    private fun updateOrAddClosestStation(
        stationInfoModelToDisplay: MutableList<CitibikeStationInformationModelDecorated>,
        closestStation: CitibikeStationInformationModelDecorated
    ) {
        val favoriteStation: CitibikeStationInformationModelDecorated? =
            stationInfoModelToDisplay.find { x -> x.model.station_id == closestStation.model.station_id }
        if (favoriteStation == null) {
            stationInfoModelToDisplay.add(0, closestStation)
        } else {
            favoriteStation.isClosest = true
        }
    }

    private fun getClosestStation(userLocation: Location): CitibikeStationInformationModelDecorated {
        var closestStation =
            _stationInformationModelMap.values.first().model
        var minDistance: Double = Double.MAX_VALUE
        for (stationInfo in _stationInformationModelMap.values.map { x -> x.model }) {
            val stationDistance = LocationUtils.computeDistance(
                userLocation.latitude,
                userLocation.longitude,
                stationInfo.lat,
                stationInfo.lon
            )
            if (stationDistance < minDistance) {
                minDistance = stationDistance
                closestStation = stationInfo
            }
        }
        return CitibikeStationInformationModelDecorated(
            closestStation,
            isClosest = true,
            isFavorite = false
        )
    }

    fun getStationLocation(stationId: Int): Location? {
        val get: CitibikeStationInformationModelDecorated? =
            _stationInformationModelMap.get(stationId)
        return get?.model?.let { Location(it.lat, it.lon, Duration.ZERO) }
    }

    fun getCitiInfoModel(stationId: Int):  CitibikeStationInformationModelDecorated?{
        val stationInfoModel = _stationInformationModelMap.get(stationId)
        if(stationInfoModel==null)
        {
            Log.e(TAG, "Unable to retrieve station info model from station id {stationId}")
            return null
        }
        return stationInfoModel
    }
}

