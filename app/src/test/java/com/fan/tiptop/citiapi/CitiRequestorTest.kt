package com.fan.tiptop.citiapi


import org.junit.Assert.*
import org.junit.Test
import java.io.File

class CitiRequestorTest {
    @Test
    fun loadStationsStatus() {
        val requestor = CitiRequestor()
        val model =
            requestor.load(File("/Users/fabriceedon/Dev/Android/DockThor/app/src/test/java/com/fan/tiptop/citiapi/gbfs_station_status.json").inputStream())
        assertNotNull(model)

    }
}