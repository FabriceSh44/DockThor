package com.fan.tiptop.citiapi

import android.util.Log
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModelDecorated
import com.fan.tiptop.citiapi.location.LocationUtils

class CitiKernel {

    //LOG
    private val TAG = "CitiKernel"

    //CITI
    private val _requester = CitiRequester()
    private var _stationInformationModelList: List<CitibikeStationInformationModelDecorated> =
        listOf()

    fun processStationInfoRequestResult(result: String) {
        try {
            _stationInformationModelList =
                _requester.getStationInformationModel(result).data.stations.map { x ->
                    CitibikeStationInformationModelDecorated(
                        x,
                        isClosest = false,
                        isFavorite = false
                    )
                }
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
        if(favoriteStation==null)
        {
            stationInfoModelToDisplay.add(0,closestStation)
        }
        else{
            favoriteStation.isClosest=true
        }
    }

    private fun getClosestStation(userLocation: Location): CitibikeStationInformationModelDecorated {
        var closestStation =
            _stationInformationModelList.first().model
        var minDistance: Double = Double.MAX_VALUE
        for (stationInfo in _stationInformationModelList.map { x->x.model }) {
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
        return CitibikeStationInformationModelDecorated(closestStation, isClosest = true, isFavorite = false)
    }

}
