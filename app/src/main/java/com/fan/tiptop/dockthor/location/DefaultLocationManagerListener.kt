package com.fan.tiptop.dockthor.location

import android.location.Location

interface DefaultLocationManagerListener {
    suspend fun getLocation(location: Location?)

}
