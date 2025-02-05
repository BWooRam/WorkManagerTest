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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import com.hyundaiht.workmanagertest.work.MyWorker1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class PeriodicWorkRegisterAndCancelActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager.getInstance(this@PeriodicWorkRegisterAndCancelActivity)

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
                        /**
                         * 테스트 결과
                         * - 앱이 켜져있을때
                         * 2025-01-03 10:35:00.909 30527-5212  MyWorker                com.hyundaiht.workmanagertest        D  doWork() isSuccess = true
                         * 2025-01-03 10:35:10.954 30527-32027 WM-WorkerWrapper        com.hyundaiht.workmanagertest        I  Worker result SUCCESS for Work [ id=fbc66d88-5fc9-4513-8f64-c73b24b3d3c6, tags={ com.hyundaiht.workmanagertest.MainActivity$MyWorker1, MyWorker1 } ]
                         * 2025-01-03 10:50:11.045 30527-6244  MyWorker                com.hyundaiht.workmanagertest        D  doWork() isSuccess = true
                         * 2025-01-03 10:50:21.071 30527-32073 WM-WorkerWrapper        com.hyundaiht.workmanagertest        I  Worker result SUCCESS for Work [ id=fbc66d88-5fc9-4513-8f64-c73b24b3d3c6, tags={ com.hyundaiht.workmanagertest.MainActivity$MyWorker1, MyWorker1 } ]
                         *
                         * - 앱이 꺼져있을때
                         * 2025-01-03 11:05:22.218  9140-9179  MyWorker                com.hyundaiht.workmanagertest        D  doWork() isSuccess = true
                         * 2025-01-03 11:05:32.277  9140-9176  WM-WorkerWrapper        com.hyundaiht.workmanagertest        I  Worker result SUCCESS for Work [ id=fbc66d88-5fc9-4513-8f64-c73b24b3d3c6, tags={ com.hyundaiht.workmanagertest.MainActivity$MyWorker1, MyWorker1 } ]
                         * 2025-01-03 11:20:32.419  9140-9849  MyWorker                com.hyundaiht.workmanagertest        D  doWork() isSuccess = true
                         * 2025-01-03 11:20:42.446  9140-9178  WM-WorkerWrapper        com.hyundaiht.workmanagertest        I  Worker result SUCCESS for Work [ id=fbc66d88-5fc9-4513-8f64-c73b24b3d3c6, tags={ com.hyundaiht.workmanagertest.MainActivity$MyWorker1, MyWorker1 } ]
                         */
                        TitleAndButton(
                            title = "PeriodicWork enqueue 작업 예약 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "작업 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request = PeriodicWorkRequestBuilder<MyWorker1>(
                                        repeatInterval = 15,
                                        repeatIntervalTimeUnit = TimeUnit.MINUTES
                                    )
                                        .addTag("PeriodicWorkTest")
                                        .setInputData(workDataOf("isSuccess" to true))
                                        .build()

                                    workManager.enqueue(request)
                                }
                            }
                        )

                        TitleAndButton(title = "PeriodicWork enqueue flex 작업 예약 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val delay = Random.nextLong(0, 1000000)
                                    val request = PeriodicWorkRequestBuilder<MyWorker1>(
                                        repeatInterval = 15,
                                        repeatIntervalTimeUnit = TimeUnit.MINUTES,
                                        flexTimeInterval = 15,
                                        flexTimeIntervalUnit = TimeUnit.MINUTES
                                    )
                                        .addTag("PeriodicWorkTest")
                                        .setInputData(workDataOf("isSuccess" to true, "delay" to delay))
                                        .build()

                                    val operation = workManager.enqueue(request)
                                    Log.d(tag, "enqueue operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "PeriodicWork enqueueUniquePeriodicWork 작업 예약 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val delay = Random.nextLong(0, 1000000)
                                    val request = PeriodicWorkRequestBuilder<MyWorker1>(
                                        repeatInterval = 15,
                                        repeatIntervalTimeUnit = TimeUnit.MINUTES,
                                        flexTimeInterval = 15,
                                        flexTimeIntervalUnit = TimeUnit.MINUTES
                                    )
                                            .setInputData(workDataOf("isSuccess" to true, "delay" to delay))
                                            .build()

                                    val operation = workManager.enqueueUniquePeriodicWork(
                                        "UniquePeriodicWork",
                                        ExistingPeriodicWorkPolicy.UPDATE,
                                        request
                                    )
                                    Log.d(tag, "enqueueUniquePeriodicWork operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "PeriodicWork enqueueUniqueWork, enqueue 작업 예약 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation1 =
                                        workManager.getWorkInfosForUniqueWork("UniquePeriodicWork")
                                    val operation2 = workManager.getWorkInfosByTag("PeriodicWorkTest")
                                    for (work in operation1.get()) {
                                        Log.d(tag, "UniquePeriodicWork work = $work")
                                    }

                                    for (work in operation2.get()) {
                                        Log.d(tag, "PeriodicWorkTest work = $work")
                                    }
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
                                    Log.d(tag, "cancelAllWorkByTag operation = ${operation.result.get()}")
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
