package com.hyundaiht.workmanagertest.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hyundaiht.workmanagertest.R
import kotlinx.coroutines.delay

/**
 * DownloadWorker
 *
 * @constructor
 * - appContext
 * - workerParams
 *
 * @param appContext
 * @param workerParams
 */
class DownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val isSuccess = inputData.getBoolean("isSuccess", true)
        val isRetry = inputData.getBoolean("isRetry", false)
        val delay = 200L
        Log.d("DownloadWorker", "doWork() isSuccess = $isSuccess")
        setForeground(createForegroundInfo("0%"))
        setProgress(workDataOf("progress" to 0))
        delay(delay)
        setForeground(createForegroundInfo("10%"))
        setProgress(workDataOf("progress" to 10))
        delay(delay)
        setForeground(createForegroundInfo("20%"))
        setProgress(workDataOf("progress" to 20))
        delay(delay)
        setForeground(createForegroundInfo("30%"))
        setProgress(workDataOf("progress" to 30))
        delay(delay)
        setForeground(createForegroundInfo("40%"))
        setProgress(workDataOf("progress" to 40))
        delay(delay)
        setForeground(createForegroundInfo("50%"))
        setProgress(workDataOf("progress" to 50))
        delay(delay)
        if (!isSuccess)
            return Result.failure(workDataOf("progress" to "download 60% failure!"))
        setForeground(createForegroundInfo("60%"))
        setProgress(workDataOf("progress" to 60))
        delay(delay)
        setForeground(createForegroundInfo("70%"))
        setProgress(workDataOf("progress" to 70))
        delay(delay)
        setForeground(createForegroundInfo("80%"))
        setProgress(workDataOf("progress" to 80))
        delay(delay)
        setForeground(createForegroundInfo("90%"))
        setProgress(workDataOf("progress" to 90))
        delay(delay)
        if (isRetry)
            return Result.retry()
        setForeground(createForegroundInfo("100%"))
        setProgress(workDataOf("progress" to 100))
        delay(delay)
        return Result.success(workDataOf("success" to 100))
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("0")
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = "TEST"

        // Create a Notification channel if necessary
        val channelId = "work_channel" // 채널 ID
        val channelName = "WorkManager Notifications" // 채널 이름
        val channelDescription = "Notifications for WorkManager tasks" // 채널 설명
        val importance = NotificationManager.IMPORTANCE_LOW // 알림 중요도

        // 채널 생성
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        // NotificationManager를 통해 채널 등록
        val notificationManager =
            applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, "work_channel")
            .setContentTitle(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(0, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(0, notification)
        }
    }

}