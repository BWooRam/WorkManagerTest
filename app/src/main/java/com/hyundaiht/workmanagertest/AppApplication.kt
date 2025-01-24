package com.hyundaiht.workmanagertest

import android.annotation.SuppressLint
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppApplication : Application(), Configuration.Provider  {
    @SuppressLint("RestrictedApi")
    private val workerFactory: WorkerFactory = HiltWorkerFactory.getDefaultWorkerFactory()

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
}