package com.fan.tiptop.citiapi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.fan.tiptop.citiapi.data.CitiStationId
import com.fan.tiptop.citiapi.data.CitibikeStationAlarm
import com.fan.tiptop.citiapi.data.CitibikeStationAlarmData
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel

@Dao
interface DockthorDao {
    @Insert
    suspend fun insert(stationInformationModel: CitibikeStationInformationModel)
    @Delete
    suspend fun delete(stationInformationModel: CitibikeStationInformationModel)

    @Query("DELETE FROM station_information_model WHERE station_id in (:stationIdList)")
    suspend fun deleteAlarmByStationId(stationIdList: List<CitiStationId>)
    @Query("SELECT * from station_information_model")
    suspend fun getFavoriteStations():List<CitibikeStationInformationModel>

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
