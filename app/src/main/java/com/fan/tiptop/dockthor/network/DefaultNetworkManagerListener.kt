package com.fan.tiptop.dockthor.network

interface DefaultNetworkManagerListener {
    fun getResult(result: String)
    fun getError(error: String)
}