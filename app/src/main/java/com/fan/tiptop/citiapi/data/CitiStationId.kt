package com.fan.tiptop.citiapi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CitiStationId (
    val value:String
): Parcelable
