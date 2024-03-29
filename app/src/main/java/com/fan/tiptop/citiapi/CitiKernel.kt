package com.fan.tiptop.citiapi

import android.util.Log
import com.fan.tiptop.citiapi.data.*
import com.fan.tiptop.citiapi.location.LocationUtils
import com.fan.tiptop.citiapi.request.CitiRequester
import kotlin.time.Duration

class CitiKernel {

    //LOG
    private val TAG = "CitiKernel"

    //CITI
    private val _requester = CitiRequester()
    private var _stationInformationModelMap: Map<CitiStationId, CitibikeStationInformationModelDecorated> =
        mapOf()

    fun processStationInfoRequestResult(result: String) {
        try {
            _stationInformationModelMap =
                _requester.getStationInformationModel(result).data.stations.map { x ->
                    CitiStationId(x.station_id) to
                            CitibikeStationInformationModelDecorated(
                                StationInformationModel(
                                    station_id = CitiStationId(x.station_id),
                                    name = "",
                                    address = x.name,
                                    isFavorite = false,
                                    expiryTime = null,
                                    capacity = x.capacity,
                                    lon = x.lon,
                                    lat = x.lat
                                )
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
    ): MutableList<CitiStationStatus> {
        try {
            if (stationInfoModelToDisplay.isNotEmpty()) {
                return _requester.getAvailabilitiesWithLocation(
                    result, stationInfoModelToDisplay,
                    userLocation
                )
            }
            return mutableListOf()
        } catch (e: Exception) {
            Log.e(TAG, "Unable to process response. Got error $e")
            return mutableListOf()
        }
    }

    fun getCitiStationStatus(result: String, stationId: CitiStationId): CitiStationStatus? {
        return _requester.getStationStatus(stationId, result)
    }

    fun getStationLocation(stationId: CitiStationId): Location? {
        val get: CitibikeStationInformationModelDecorated? =
            _stationInformationModelMap[stationId]
        return get?.model?.let { Location(it.lat, it.lon, Duration.ZERO) }
    }

    fun getCitiInfoModel(stationId: CitiStationId): CitibikeStationInformationModelDecorated? {
        val stationInfoModel = _stationInformationModelMap[stationId]
        if (stationInfoModel == null) {
            Log.e(TAG, "Unable to retrieve station info model from station id {stationId}")
            return null
        }
        return stationInfoModel
    }

    fun getClosestStationWithCriteria(
        userLocation: Location,
        targetCitiStationStatus: CitiStationStatus,
        criteria: StationSearchCriteria,
        result: String
    ): CitiStationStatus? {
        val minToReplace =
            if (criteria == StationSearchCriteria.CLOSEST_WITH_BIKE) targetCitiStationStatus.numBikeAvailable.toInt() else targetCitiStationStatus.numDockAvailable.toInt()
        val stationIdsWithCriteria =
            _requester.getStationStatusWithCriteria(result, criteria, minToReplace)
        val citiStationModelToReplace =
            _stationInformationModelMap[targetCitiStationStatus.stationId]
        if (citiStationModelToReplace != null) {
            val sortedCitiModelList = LocationUtils.getDropShapedClosestStation(
                _stationInformationModelMap.filter {
                    stationIdsWithCriteria.containsKey(
                        it.value.model.station_id
                    )
                }.values,
                userLocation,
                citiStationModelToReplace.model.toCitiLocation()
            )
            val closestStationInfo = sortedCitiModelList.firstOrNull() ?: return null
            val status =
                stationIdsWithCriteria.get(closestStationInfo.model.station_id)
            status?.let {
                it.address = closestStationInfo.model.address
                it.distance = LocationUtils.computeAndFormatDistance(
                    userLocation.latitude,
                    userLocation.longitude,
                    closestStationInfo.model.lat,
                    closestStationInfo.model.lon
                )
            }
            return status
        }
        return null
    }

}


private fun StationInformationModel.toCitiLocation(): Location {
    return Location(this.lat, this.lon)
}

