package com.fan.tiptop.citiapi.data

import kotlin.time.Duration

data class Location(val latitude: Double, val longitude: Double, val ageLocation: Duration=Duration.ZERO){ }
