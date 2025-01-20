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
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import com.hyundaiht.workmanagertest.work.MyWorker1
import com.hyundaiht.workmanagertest.work.MyWorker2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OneTimeWorkPauseAndCancelActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager.getInstance(this@OneTimeWorkPauseAndCancelActivity)

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
                        TitleAndButton(title = "enqueue List<Work> 작업 예약 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request1 =
                                        OneTimeWorkRequestBuilder<MyWorker1>()
                                            .addTag("MyWorkerPauseTest")
                                            .setInputData(workDataOf("isSuccess" to true, "delay" to 1000000L))
                                            .build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .addTag("MyWorkerPauseTest")
                                            .setInputData(workDataOf("isSuccess" to true, "delay" to 1000000L)).build()

                                    val operation1 = workManager.enqueue(
                                        mutableListOf(request1, request2)
                                    )
                                    Log.d(tag, "enqueue operation = ${operation1.result.get()}")
                                }
                            })

                        TitleAndButton(title = "cancelAllWorkByTag 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation = workManager.cancelAllWorkByTag("MyWorkerPauseTest")
                                    Log.d(tag, "cancelAllWorkByTag operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "enqueueUniqueWork List<Work> 작업 예약 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val request1 =
                                        OneTimeWorkRequestBuilder<MyWorker1>()
                                            .setInputData(workDataOf("isSuccess" to true, "delay" to 1000000L)).build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .setInputData(workDataOf("isSuccess" to true, "delay" to 1000000L)).build()

                                    val operation = workManager.enqueueUniqueWork(
                                        "UniqueWorkPauseTest",
                                        ExistingWorkPolicy.REPLACE,
                                        mutableListOf(request1, request2)
                                    )
                                    Log.d(tag, "enqueueUniqueWork operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "cancelUniqueWork 작업 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation = workManager.cancelUniqueWork("UniqueWorkPauseTest")
                                    Log.d(tag, "cancelUniqueWork operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "enqueue, enqueueUniqueWork 작업 예약 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation1 = workManager.getWorkInfosByTag("MyWorkerPauseTest")
                                    val operation2 = workManager.getWorkInfosForUniqueWork("UniqueWorkPauseTest")
                                    for(work in operation1.get()){
                                        Log.d(tag, "getWorkInfosByTag MyWorkerTest work = $work")
                                    }
                                    for(work in operation2.get()){
                                        Log.d(tag, "getWorkInfosForUniqueWork MyWorkerTest work = $work")
                                    }
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
                                    workManager.pruneWork()
                                }
                            })
                    }
                }
            }
        }
    }
}
