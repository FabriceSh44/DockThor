package com.fan.tiptop.citiapi


import org.junit.Assert.*
import org.junit.Test
import java.io.File

class CitiRequestorTest {
    val stationStatusFile = javaClass.classLoader.getResource("gbfs_station_status.json").file
    val stationInformationFile = javaClass.classLoader.getResource("station_information.json").file

    @Test
    fun getAvailabilities() {
        val requestor = CitiRequestor()
        val stationId = 83
        val result =requestor.getAvailabilities(File(stationStatusFile).readText(), stationId)
        assertEquals("35/24",result )
    }

    @Test
    fun getStationInformation(){
        val requestor = CitiRequestor()
        val model= requestor.getStationInformationModel(File(stationInformationFile).readText())
        assertNotNull(model)
    }
}