package com.hyundaiht.workmanagertest

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log

class NetworkState(
    private val context: Context
) {
    private val tag = javaClass.simpleName
    private val connectivityManager: ConnectivityManager? =
        context.getSystemService(ConnectivityManager::class.java)

    /**
     * NetworkType
     *
     */
    enum class NetworkType {
        WiFi,
        Cellular,
        Ethernet,
        Other
    }

    fun getActiveNetwork(): Network? = connectivityManager?.activeNetwork

    fun getActiveNetworkInfo(): Pair<NetworkCapabilities?, LinkProperties?> {
        val activeNetwork =
            getActiveNetwork() ?: throw NullPointerException("activeNetwork is null")
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork) // NetworkCapabilities
        val linkProperties = connectivityManager?.getLinkProperties(activeNetwork) // LinkProperties
        return caps to linkProperties
    }

    fun getCurrentNetworkType(): NetworkType {
        val networkInfo = getActiveNetworkInfo()
        val capabilities = networkInfo.first ?: throw NullPointerException("NetworkCapabilities is null")

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WiFi
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.Cellular
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.Ethernet
            else -> NetworkType.Other
        }
    }
}