package com.fan.tiptop.citiapi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel

@Dao
interface DockthorDao {
    @Insert
    suspend fun insert(stationInformationModel: CitibikeStationInformationModel)
    @Delete
    suspend fun delete(stationInformationModel: CitibikeStationInformationModel)

    @Query("DELETE FROM station_information_model WHERE station_id in (:stationIdList)")
    suspend fun deleteByStationId(stationIdList: List<Int>)
    @Query("SELECT * from station_information_model")
    suspend fun getFavoriteStations():List<CitibikeStationInformationModel>

     @Insert
    suspend fun insert(stationAlarm: CitibikeStationAlarm)
    @Delete
    suspend fun delete(stationAlarm: CitibikeStationAlarm)

    @Query("DELETE from station_alarm WHERE stationId = :stationId")
    suspend fun deleteByStationId(stationId:Int)
    @Query("SELECT * from station_alarm WHERE stationId = :stationId")
    suspend fun getStationAlarms(stationId:Int):List<CitibikeStationAlarm>
}