package com.hyundaiht.workmanagertest.work

import android.content.Context
import android.util.Log
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture

/**
 * FinishWorker
 *
 * @constructor
 * - appContext
 * - workerParams
 *
 * @param appContext
 * @param workerParams
 */
class FinishWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val isSuccess = inputData.getBoolean("isSuccess", true)
        val mergedData = inputData.keyValueMap
        val workStringArray = inputData.getStringArray("work").contentToString()
        val workString = inputData.getString("work")
        Log.d("FinishWorker", "workString: $workString, workStringArray: $workStringArray")

        mergedData.forEach { (key, value) ->
            Log.d("FinishWorker", "Key: $key, Value: $value")
        }
        Log.d("FinishWorker", "doWork() isSuccess = $isSuccess")
        Thread.sleep(1000)
        return if (isSuccess) Result.success(workDataOf("work" to "FinishWorker")) else Result.failure()
    }

    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        return super.getForegroundInfoAsync()
    }
}