package com.fan.tiptop.citiapi


import org.junit.Assert.*
import org.junit.Test
import java.io.File

class CitiRequestorTest {
    val stationStatusFile = javaClass.classLoader.getResource("gbfs_station_status.json").file
    val stationInformationFile = javaClass.classLoader.getResource("station_information.json").file

    @Test
    fun getAvailabilities() {
        val requestor = CitiRequester()
        val stationId = 83
        val result = requestor.getAvailabilities(File(stationStatusFile).readText(), stationId)
        assertEquals("35 bikes\n24 docks", result)
    }

    @Test
    fun getStationInformation() {
        val requestor = CitiRequester()
        val model = requestor.getStationInformationModel(File(stationInformationFile).readText())
        assertNotNull(model)
    }
}