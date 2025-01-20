package com.hyundaiht.workmanagertest.work

import android.content.Context
import android.util.Log
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture

/**
 * MyWorker
 *
 * @constructor
 * - appContext
 * - workerParams
 *
 * @param appContext
 * @param workerParams
 */
class MyWorker2(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    private val tag = javaClass.simpleName

    override fun doWork(): Result {
        val isSuccess = inputData.getBoolean("isSuccess", true)
        val delayValue = inputData.getLong("delay", 10000)
        Log.d(tag, "doWork() delayValue = $delayValue, isSuccess = $isSuccess")
        Thread.sleep(delayValue)
        return if (isSuccess) Result.success(workDataOf("work" to "MyWorker2")) else Result.failure()
    }

    override fun onStopped() {
        super.onStopped()
        Log.d(tag, "onStopped()")
    }

    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        return super.getForegroundInfoAsync()
    }
}