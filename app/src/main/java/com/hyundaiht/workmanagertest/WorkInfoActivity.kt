package com.hyundaiht.workmanagertest

import android.content.Context
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
import androidx.work.ArrayCreatingInputMerger
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OverwritingInputMerger
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import com.hyundaiht.workmanagertest.work.DownloadWorker
import com.hyundaiht.workmanagertest.ui.theme.WorkManagerTestTheme
import com.hyundaiht.workmanagertest.work.FinishWorker
import com.hyundaiht.workmanagertest.work.MyWorker1
import com.hyundaiht.workmanagertest.work.MyWorker2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WorkInfoActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager
            .getInstance(this@WorkInfoActivity)

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
                                val workerInfo = WorkManager.getInstance(this@WorkInfoActivity)
                                    .getWorkInfosForUniqueWork(query).get()
                                Log.d("MainActivity", "query = $query, workerInfo = $workerInfo")

                                var string = ""
                                for (info in workerInfo) {
                                    string += info
                                }
                                string
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
                            title = "중간 worker 진행률 관찰 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "DownloadWorker 관찰 작업 실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    WorkManager.getInstance(applicationContext)
                                        // requestId is the WorkRequest id
                                        .getWorkInfosForUniqueWorkLiveData("DownloadWorker")
                                        .observe(
                                            this@WorkInfoActivity
                                        ) { workInfos: List<WorkInfo> ->
                                            for (workInfo in workInfos) {
                                                val progress = workInfo.progress
                                                val value = progress.getInt("progress", 0)
                                                Log.d(
                                                    "MainActivity",
                                                    "progress workInfo = $workInfo, value = $value"
                                                )
                                            }
                                        }
                                }
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
                    }
                }
            }
        }
    }
}