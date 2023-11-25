package com.fan.tiptop.citiapi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.fan.tiptop.citiapi.data.*

@Dao
interface DockthorDao {
    @Insert
    suspend fun insert(stationInformationModel: StationInformationModel)
    @Query("UPDATE station_information_model SET name=:name WHERE station_id = :stationId")
    suspend fun updateName(name:String, stationId: CitiStationId)
    @Delete
    suspend fun delete(stationInformationModel: StationInformationModel)

    @Query("DELETE FROM station_information_model WHERE station_id in (:stationIdList)")
    suspend fun deleteStationsByStationId(stationIdList: List<CitiStationId>)
    @Query("SELECT * from station_information_model")
    suspend fun getFavoriteStations():List<StationInformationModel>

    @Insert
    suspend fun insert(stationAlarm: CitibikeStationAlarm)
    @Delete
    suspend fun delete(stationAlarm: CitibikeStationAlarm)

    @Query("SELECT * from station_alarm WHERE stationId = :stationId")
    suspend fun getStationAlarms(stationId:CitiStationId):List<CitibikeStationAlarm>

    @Insert
    suspend fun insert(stationAlarmData: CitibikeStationAlarmData)
    @Upsert
    suspend fun upsert(stationAlarmData: CitibikeStationAlarmData)
    @Delete
    suspend fun delete(stationAlarmData: CitibikeStationAlarmData)
    @Query("SELECT * from station_alarm_data WHERE stationId = :stationId")
    suspend fun getStationAlarmData(stationId:CitiStationId):CitibikeStationAlarmData?
}
