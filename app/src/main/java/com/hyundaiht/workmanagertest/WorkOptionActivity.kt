package com.hyundaiht.workmanagertest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hyundaiht.workmanagertest.work.DownloadWorker
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import com.hyundaiht.workmanagertest.work.MyWorker1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class WorkOptionActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager
            .getInstance(this@WorkOptionActivity)

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
                            title = "신속 처리 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val workTag = "WorkerExpeditedTest"
                                    val id = Random.nextInt(0, 10000000)

                                    val request = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .addTag(workTag)
                                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                        .setInputData(workDataOf("id" to id))
                                        .build()

                                    val operation = workManager.enqueue(request)
                                    Log.d(
                                        workTag,
                                        "WorkerExpeditedTest operation = ${operation.result.get()}"
                                    )
                                }
                            })

                        TitleAndButton(
                            title = "신속 처리 작업 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val workTag = "WorkerExpeditedTest"
                                    val workInfos = workManager.getWorkInfosByTag(workTag)
                                    for (info in workInfos.get()) {
                                        Log.d(workTag, "WorkerExpeditedTest info = $info")
                                    }
                                }
                            })

                        TitleAndButton(
                            title = "지연된 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                                        .addTag("DownloadWorker")
                                        .setInitialDelay(5, TimeUnit.SECONDS)
                                        .build()

                                    workManager.enqueue(request)
                                }
                            })

                        TitleAndButton(
                            title = "입력 데이터 할당 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .setInputData(
                                            workDataOf(
                                                "id" to Random.nextInt(),
                                                "isSuccess" to true,
                                                "delay" to 1000.toLong()
                                            )
                                        )
                                        .setBackoffCriteria(
                                            backoffPolicy = BackoffPolicy.LINEAR,
                                            backoffDelay = 10L,
                                            timeUnit = TimeUnit.SECONDS
                                        )
                                        .build()

                                    workManager.enqueue(request)
                                }
                            })

                        TitleAndButton(
                            title = "작업에 태그 지정 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "다운로드 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request = OneTimeWorkRequestBuilder<MyWorker1>()
                                        .addTag("TAG1")
                                        .addTag("TAG2")
                                        .addTag("TAG3")
                                        .setInputData(workDataOf("id" to Random.nextInt()))
                                        .build()

                                    workManager.enqueue(request)
                                }
                            })

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
//                                        .setRequiresDeviceIdle()    //WorkRequest를 실행하기 위해 장치가 유휴 상태여야 하는지 여부를 설정합니다. 기본값은 false입니다.
//                                        .setRequiresStorageNotLow() //WorkRequest를 실행하기 위해 장치의 사용 가능한 저장소가 허용 가능한 수준이어야 하는지 여부를 설정합니다. 기본값은 false입니다.
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
                            })

                        TitleAndButton(title = "PeriodicWork cancelAllWork 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation = workManager.cancelAllWork()
                                    Log.d(
                                        tag,
                                        "cancelAllWorkByTag operation = ${operation.result.get()}"
                                    )
                                }
                            })

                        TitleAndButton(title = "pruneWork 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation = workManager.pruneWork()
                                    Log.d(tag, "pruneWork operation = ${operation.result.get()}")
                                }
                            })
                    }
                }
            }
        }
    }
}