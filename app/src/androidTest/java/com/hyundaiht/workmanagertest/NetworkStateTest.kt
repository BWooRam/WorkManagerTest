package com.hyundaiht.workmanagertest

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test


class NetworkStateTest {
    private var appContext: Context? = null
    private var networkState: NetworkState? = null

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        if (appContext != null)
            networkState = NetworkState(appContext!!)
    }

    @Test
    fun getActiveNetwork() {
        val network = networkState?.getActiveNetwork()
        println("getActiveNetwork appContext = $appContext, network = $network")
    }

    @Test
    fun getActiveNetworkInfo() {
        val info = networkState?.getActiveNetworkInfo()
        println("getActiveNetwork appContext = $appContext, info = $info")
    }

    @Test
    fun getCurrentNetworkType(){
        val networkType = networkState?.getCurrentNetworkType()
        println("getActiveNetwork appContext = $appContext, networkType = $networkType")
    }

}