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
import androidx.work.ArrayCreatingInputMerger
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OverwritingInputMerger
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.hyundaiht.workmanagertest.work.DownloadWorker
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import com.hyundaiht.workmanagertest.work.FinishWorker
import com.hyundaiht.workmanagertest.work.MyWorker1
import com.hyundaiht.workmanagertest.work.MyWorker2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LongTimeWorkAndChainingActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager
            .getInstance(this@LongTimeWorkAndChainingActivity)

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
                                    val operation =
                                        workManager.beginWith(listOf(request1, request2))
                                            .then(request3).enqueue()
                                    Log.d("MainActivity", "병렬 작업 실행 operation = $operation")
                                    Log.d(
                                        "MainActivity",
                                        "병렬 작업 실행 operation result = ${operation.result.get()}"
                                    )
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
                                    val operation =
                                        workManager.beginWith(listOf(request1, request2))
                                            .then(request3).enqueue()
                                    Log.d("MainActivity", "병렬 작업 실행 operation = $operation")
                                    Log.d(
                                        "MainActivity",
                                        "병렬 작업 실행 operation result = ${operation.result.get()}"
                                    )
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
                                    val operation = workManager.beginWith(request2).then(request1)
                                        .then(request3).enqueue()
                                    Log.d(
                                        "MainActivity",
                                        "Dependent work 실행 operation = $operation"
                                    )
                                    Log.d(
                                        "MainActivity",
                                        "Dependent work 실행 operation result = ${operation.await()}"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}