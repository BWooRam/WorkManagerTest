package com.hyundaiht.workmanagertest.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class HiltWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    private val tag = javaClass.simpleName

    override fun doWork(): Result {
        val isSuccess = inputData.getBoolean("isSuccess", true)
        val id = inputData.getInt("id", 0)
        val delayValue = inputData.getLong("delay", 10000)
        Log.d(tag, "doWork() id = $id, delayValue = $delayValue, isSuccess = $isSuccess")
        Thread.sleep(delayValue)
        return if (isSuccess) Result.success(workDataOf("work" to "MyWorker1")) else Result.failure()
    }

    override fun onStopped() {
        super.onStopped()
        Log.d(tag, "onStopped()")
    }


    override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
        return super.getForegroundInfoAsync()
    }
}