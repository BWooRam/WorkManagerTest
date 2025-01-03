package com.hyundaiht.workmanagertest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.work.ArrayCreatingInputMerger
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OverwritingInputMerger
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager
            .getInstance(this@MainActivity)

        setContent {
            WorkManagerTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                    ) {
                        TitleAndButton(
                            title = "enqueueUniqueWork 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "즉시 테스트 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request =
                                        OneTimeWorkRequestBuilder<MyWorker1>()
                                            .build()

                                    workManager.enqueueUniqueWork(
                                        "MyWork",
                                        ExistingWorkPolicy.KEEP,
                                        arrayListOf(request, request, request)
                                    )
                                }

                            }
                        )

                        TitleAndButtonAndSearch(
                            title = "enqueueUniqueWork 작업 정보 확인",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 정보 조회 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = { query ->
                                val workerInfo = WorkManager.getInstance(this@MainActivity)
                                    .getWorkInfosForUniqueWork(query).get()
                                Log.d("MainActivity", "query = $query, workerInfo = $workerInfo")

                                var string = ""
                                for (info in workerInfo) {
                                    string += info
                                }
                                string
                            }
                        )

                        TitleAndButton(
                            title = "작업 삭제 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 삭제 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                workManager.pruneWork()
                            }
                        )

                        TitleAndButton(
                            title = "DownloadWorker 작업 중지 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 중지 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                workManager.cancelUniqueWork("DownloadWorker")
                            }
                        )

                        TitleAndButton(
                            title = "지속적인 작업 - 장기 실행",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val isSuccess = true
                                    Log.d("MainActivity", "DownloadWorker isSuccess = $isSuccess")
                                    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                                        .setInputData(workDataOf("isSuccess" to isSuccess))
                                        .build()

                                    workManager.enqueueUniqueWork(
                                        "DownloadWorker",
                                        ExistingWorkPolicy.KEEP,
                                        request
                                    )
                                }
                            }
                        )

                        TitleAndButton(
                            title = "작업 제약 조건 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    //사용자의 기기가 충전 중이고 Wi-Fi에 연결되어 있을 때만 실행
                                    val isSuccess = true
                                    val constraints = Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.UNMETERED)
                                        .setRequiresCharging(true)
                                        .build()

                                    Log.d("MainActivity", "DownloadWorker isSuccess = $isSuccess")
                                    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                                        .setInputData(workDataOf("isSuccess" to isSuccess))
                                        .setConstraints(constraints)
                                        .build()

                                    workManager.enqueueUniqueWork(
                                        "DownloadWorker",
                                        ExistingWorkPolicy.KEEP,
                                        request
                                    )
                                }
                            }
                        )

                        TitleAndButton(
                            title = "재시도 및 백오프 정책 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val isRetry = true
                                    Log.d("MainActivity", "DownloadWorker isRetry = $isRetry")

                                    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                                        .setInputData(workDataOf("isRetry" to isRetry))
                                        .setBackoffCriteria(
                                            backoffPolicy = BackoffPolicy.LINEAR,
                                            backoffDelay = 10L,
                                            timeUnit = TimeUnit.SECONDS
                                        )
                                        .build()

                                    workManager.enqueueUniqueWork(
                                        "DownloadWorker",
                                        ExistingWorkPolicy.KEEP,
                                        request
                                    )
                                }
                            }
                        )

                        TitleAndButtonAndSearch(
                            title = "복잡한 작업 쿼리 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 정보 조회 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = { query ->
                                val item = query.split(",").toList()
                                Log.d("MainActivity", "query = $query, item = $item")

                                val workQuery = WorkQuery.Builder
                                    .fromTags(item)
                                    .build()

                                val workerInfo = workManager.getWorkInfos(workQuery).get()
                                Log.d("MainActivity", "workerInfo = $workerInfo")

                                var string = ""
                                for (info in workerInfo) {
                                    string += info
                                }
                                string
                            }
                        )

                        TitleAndButton(
                            title = "작업 체이닝 테스트 - ArrayCreatingInputMerger 병렬 작업 실행",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request1 = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .addTag("MyWorker1")
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .build()

                                    val request2 = OneTimeWorkRequestBuilder<MyWorker2>()
                                        .addTag("MyWorker2")
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .build()

                                    val request3 = OneTimeWorkRequestBuilder<FinishWorker>()
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .setInputMerger(ArrayCreatingInputMerger::class.java)
                                        .addTag("FinishWorker")
                                        .build()

                                    //MyWorker,DownloadWorker,FinishWorker
                                    val operation = workManager.beginWith(listOf(request1, request2)).then(request3).enqueue()
                                    Log.d("MainActivity", "병렬 작업 실행 operation = $operation")
                                    Log.d("MainActivity", "병렬 작업 실행 operation result = ${operation.result.get()}")
                                }
                            }
                        )
                        TitleAndButton(
                            title = "작업 체이닝 테스트 - OverwritingInputMerger 병렬 작업 실행",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request1 = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .addTag("MyWorker1")
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .build()

                                    val request2 = OneTimeWorkRequestBuilder<MyWorker2>()
                                        .addTag("MyWorker2")
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .build()

                                    val request3 = OneTimeWorkRequestBuilder<FinishWorker>()
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .setInputMerger(OverwritingInputMerger::class.java)
                                        .addTag("FinishWorker")
                                        .build()

                                    //MyWorker,DownloadWorker,FinishWorker
                                    val operation = workManager.beginWith(listOf(request1, request2)).then(request3).enqueue()
                                    Log.d("MainActivity", "병렬 작업 실행 operation = $operation")
                                    Log.d("MainActivity", "병렬 작업 실행 operation result = ${operation.result.get()}")
                                }
                            }
                        )

                        TitleAndButton(
                            title = "작업 체이닝 테스트 - Dependent work 실행",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request1 = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .addTag("MyWorker")
                                        .build()

                                    val request2 = OneTimeWorkRequestBuilder<DownloadWorker>()
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .addTag("DownloadWorker")
                                        .build()

                                    val request3 = OneTimeWorkRequestBuilder<FinishWorker>()
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .addTag("FinishWorker")
                                        .build()

                                    //MyWorker,DownloadWorker,FinishWorker
                                    val operation = workManager.beginWith(request2).then(request1).then(request3).enqueue()
                                    Log.d("MainActivity", "Dependent work 실행 operation = $operation")
                                    Log.d("MainActivity", "Dependent work 실행 operation result = ${operation.await()}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

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
    class MyWorker1(
        appContext: Context,
        workerParams: WorkerParameters
    ) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val isSuccess = inputData.getBoolean("isSuccess", true)
            Log.d("MyWorker", "doWork() isSuccess = $isSuccess")
            Thread.sleep(10000)
            return if(isSuccess) Result.success(workDataOf("work" to "MyWorker1")) else Result.failure()
        }

        override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
            return super.getForegroundInfoAsync()
        }
    }

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
        override fun doWork(): Result {
            val isSuccess = inputData.getBoolean("isSuccess", true)
            Log.d("MyWorker", "doWork() isSuccess = $isSuccess")
            Thread.sleep(10000)
            return if(isSuccess) Result.success(workDataOf("work" to "MyWorker2")) else Result.failure()
        }

        override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
            return super.getForegroundInfoAsync()
        }
    }

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
            return if(isSuccess) Result.success(workDataOf("work" to "FinishWorker")) else Result.failure()
        }

        override fun getForegroundInfoAsync(): ListenableFuture<ForegroundInfo> {
            return super.getForegroundInfoAsync()
        }
    }

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
            delay(delay)
            setForeground(createForegroundInfo("10%"))
            delay(delay)
            setForeground(createForegroundInfo("20%"))
            delay(delay)
            setForeground(createForegroundInfo("30%"))
            delay(delay)
            setForeground(createForegroundInfo("40%"))
            delay(delay)
            setForeground(createForegroundInfo("50%"))
            delay(delay)
            if (!isSuccess)
                return Result.failure(workDataOf("progress" to "download 60% failure!"))
            setForeground(createForegroundInfo("60%"))
            delay(delay)
            setForeground(createForegroundInfo("70%"))
            delay(delay)
            setForeground(createForegroundInfo("80%"))
            delay(delay)
            setForeground(createForegroundInfo("90%"))
            delay(delay)
            if (isRetry)
                return Result.retry()
            setForeground(createForegroundInfo("100%"))
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

}

@Composable
fun TitleAndButtonAndSearch(
    title: String,
    titleModifier: Modifier = Modifier,
    buttonName: String,
    buttonModifier: Modifier = Modifier,
    clickEvent: (query: String) -> String
) {
    var query by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }

    Text(
        text = title,
        modifier = titleModifier
    )
    TextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
    Button(
        onClick = { detail = clickEvent.invoke(query) },
        modifier = buttonModifier,
        content = { Text(text = buttonName) }
    )
    Text(
        text = detail,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .background(color = Color.Cyan)
    )
}

@Composable
fun TitleAndButton(
    title: String,
    titleModifier: Modifier = Modifier,
    buttonName: String,
    buttonModifier: Modifier = Modifier,
    clickEvent: () -> Unit
) {
    Text(
        text = title,
        modifier = titleModifier
    )
    Button(
        onClick = clickEvent,
        modifier = buttonModifier,
        content = { Text(text = buttonName) }
    )
}