package com.fan.tiptop.dockthor.network

interface DefaultNetworkManagerListener {
    suspend fun getResult(result: String)
    suspend fun getError(error: String)
}