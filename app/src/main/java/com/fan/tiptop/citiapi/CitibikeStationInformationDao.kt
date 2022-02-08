package com.fan.tiptop.citiapi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CitibikeStationInformationDao {
    @Insert
    suspend fun insert(stationInformationModel: CitibikeStationInformationModel)
    @Delete
    suspend fun delete(stationInformationModel: CitibikeStationInformationModel)

    @Query("DELETE FROM station_information_model WHERE station_id in (:stationIdList)")
    suspend fun deleteByStationId(stationIdList: List<Int>)
    @Query("SELECT * from station_information_model")
    suspend fun getFavoriteStations():List<CitibikeStationInformationModel>
}