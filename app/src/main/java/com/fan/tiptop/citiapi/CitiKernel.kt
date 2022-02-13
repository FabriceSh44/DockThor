package com.fan.tiptop.citiapi

import android.util.Log
import com.fan.tiptop.citiapi.data.CitiStationStatus
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel
import com.fan.tiptop.citiapi.location.LocationUtils

class CitiKernel {

    //LOG
    private val TAG = "CitiKernel"

    //CITI
    private val _requester = CitiRequester()
    private var _stationInformationModelList:List<CitibikeStationInformationModel> =listOf()

    fun processStationInfoRequestResult(result: String) {
        try {
            _stationInformationModelList = _requester.getStationInformationModel(result).data.stations
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error ${e}")
        }
    }

    fun getCitiStationStatusToDisplay(
        result: String,
        stationInfoModelToDisplay: MutableList<CitibikeStationInformationModel>,
        userLocation:Location?
    ): List<CitiStationStatus> {
        if(userLocation!=null)
        stationInfoModelToDisplay.add(0,getClosestStation(userLocation))
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

    private fun getClosestStation(userLocation: Location): CitibikeStationInformationModel {
        var closestStation:CitibikeStationInformationModel= _stationInformationModelList.first()
        var minDistance :Double = Double.MAX_VALUE
        for(stationInfo in _stationInformationModelList)
        {
            val stationDistance = LocationUtils.computeDistance(
                userLocation.latitude,
                userLocation.longitude,
                stationInfo.lat,
                stationInfo.lon
            )
            if(stationDistance < minDistance)
            {
                minDistance=stationDistance
                closestStation=stationInfo
            }
        }
        return closestStation
    }

}
