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

class OneTimeWorkRegisterActivity : ComponentActivity() {
    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val workManager = WorkManager.getInstance(this@OneTimeWorkRegisterActivity)

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
                        TitleAndButton(title = "OneTimeWork enqueue 작업 예약 테스트",
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
                                            .addTag("MyWorkerTest")
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .addTag("MyWorkerTest")
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    /*
                                        enqueue List<Work>와 동작 차이가 없는 것으로 보임
                                     */
                                    val operation1 = workManager.enqueue(request1)
                                    val operation2 = workManager.enqueue(request2)
                                    Log.d(tag, "operation1 = ${operation1.result.get()}")
                                    Log.d(tag, "operation2 = ${operation2.result.get()}")
                                }
                            })

                        TitleAndButton(title = "OneTimeWork enqueue List<Work> 작업 예약 테스트",
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
                                            .addTag("MyWorkerTest")
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .addTag("MyWorkerTest")
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    /*
                                        - Work1 = WorkInfo{id='4c96f2c2-2384-48b4-98fc-ef3a2dc86490', state=RUNNING, outputData=Data {}, tags=[com.hyundaiht.workmanagertest.work.MyWorker2, MyWorkerTest], progress=Data {}, runAttemptCount=1, generation=0, constraints=Constraints{requiredNetworkType=NOT_REQUIRED, requiresCharging=false, requiresDeviceIdle=false, requiresBatteryNotLow=false, requiresStorageNotLow=false, contentTriggerUpdateDelayMillis=-1, contentTriggerMaxDelayMillis=-1, contentUriTriggers=[], }, initialDelayMillis=0, periodicityInfo=null, nextScheduleTimeMillis=9223372036854775807}, stopReason=-256
                                        - Work2 = WorkInfo{id='9ec7c5bf-f8de-46e6-b2f7-c5e9f5ce7778', state=RUNNING, outputData=Data {}, tags=[com.hyundaiht.workmanagertest.work.MyWorker1, MyWorkerTest], progress=Data {}, runAttemptCount=1, generation=0, constraints=Constraints{requiredNetworkType=NOT_REQUIRED, requiresCharging=false, requiresDeviceIdle=false, requiresBatteryNotLow=false, requiresStorageNotLow=false, contentTriggerUpdateDelayMillis=-1, contentTriggerMaxDelayMillis=-1, contentUriTriggers=[], }, initialDelayMillis=0, periodicityInfo=null, nextScheduleTimeMillis=9223372036854775807}, stopReason=-256
                                        -> 결론 : List로 한다고 하나의 id로 돌지 않음, 시작과 동시에 RUNNING 상태로 바뀌는 걸로 봐서는 병렬 실행으로 보임
                                     */
                                    val operation1 = workManager.enqueue(
                                        mutableListOf(request1, request2)
                                    )
                                    Log.d(tag, "operation = ${operation1.result.get()}")
                                }
                            })

                        TitleAndButton(title = "OneTimeWork enqueue List<Work> 작업 예약 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation = workManager.getWorkInfosByTag("MyWorkerTest")
                                    for(work in operation.get()){
                                        Log.d(tag, "MyWorkerTest work = $work")
                                    }
                                }
                            })

                        TitleAndButton(title = "OneTimeWork enqueueUniqueWork, enqueue 작업 예약 테스트",
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
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .addTag("MyWorker2")
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    val operation1 = workManager.enqueueUniqueWork(
                                        "UniqueWork1",
                                        ExistingWorkPolicy.REPLACE,
                                        mutableListOf(request1)
                                    )
                                    val operation2 = workManager.enqueue(request2)
                                    Log.d(tag, "operation1 = ${operation1.result.get()}")
                                    Log.d(tag, "operation2 = ${operation2.result.get()}")
                                }
                            })

                        TitleAndButton(title = "OneTimeWork enqueueUniqueWork, enqueue 작업 예약 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val operation1 = workManager.getWorkInfosForUniqueWork("UniqueWork1")
                                    val operation2 = workManager.getWorkInfosByTag("MyWorker2")
                                    for(work in operation1.get()){
                                        Log.d(tag, "operation1 work = $work")
                                    }

                                    for (work in operation2.get()){
                                        Log.d(tag, "operation2 work = $work")
                                    }
                                }
                            })

                        /**
                         * 고유 작업은 특정 이름의 작업 인스턴스가 한 번에 하나만 있도록 보장하는 강력한 개념입니다.
                         * ID와는 달리 고유 이름은 사람이 읽을 수 있으며 WorkManager에서 자동으로 생성하는 대신 개발자가 지정합니다.
                         * 고유 이름은 태그와 달리 단일 작업 인스턴스에만 연결됩니다.
                         */
                        TitleAndButton(title = "OneTimeWork enqueueUniqueWork List<Work> 작업 예약 테스트",
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
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    val request2 =
                                        OneTimeWorkRequestBuilder<MyWorker2>()
                                            .setInputData(workDataOf("isSuccess" to true)).build()

                                    /*
                                        - Work1 : WorkInfo{id='6e56f75d-b689-4290-bb99-4574f7ddf3df', state=RUNNING, outputData=Data {}, tags=[com.hyundaiht.workmanagertest.work.MyWorker2], progress=Data {}, runAttemptCount=1, generation=0, constraints=Constraints{requiredNetworkType=NOT_REQUIRED, requiresCharging=false, requiresDeviceIdle=false, requiresBatteryNotLow=false, requiresStorageNotLow=false, contentTriggerUpdateDelayMillis=-1, contentTriggerMaxDelayMillis=-1, contentUriTriggers=[], }, initialDelayMillis=0, periodicityInfo=null, nextScheduleTimeMillis=9223372036854775807}, stopReason=-256
                                        - Work2 : WorkInfo{id='aa5417cf-04d4-412e-ac21-2fd7a9f686fc', state=RUNNING, outputData=Data {}, tags=[com.hyundaiht.workmanagertest.work.MyWorker1], progress=Data {}, runAttemptCount=1, generation=0, constraints=Constraints{requiredNetworkType=NOT_REQUIRED, requiresCharging=false, requiresDeviceIdle=false, requiresBatteryNotLow=false, requiresStorageNotLow=false, contentTriggerUpdateDelayMillis=-1, contentTriggerMaxDelayMillis=-1, contentUriTriggers=[], }, initialDelayMillis=0, periodicityInfo=null, nextScheduleTimeMillis=9223372036854775807}, stopReason=-256
                                        -> 결론 : List로 한다고 하나의 id로 돌지 않음, 시작과 동시에 RUNNING 상태로 바뀌는 걸로 봐서는 병렬 실행으로 보임
                                     */
                                    val operation = workManager.enqueueUniqueWork(
                                        "UniqueWork2",
                                        ExistingWorkPolicy.REPLACE,
                                        mutableListOf(request1, request2)
                                    )
                                    Log.d(tag, "operation = ${operation.result.get()}")
                                }
                            })

                        TitleAndButton(title = "OneTimeWork enqueueUniqueWork List<Work> 작업 예약 확인 테스트",
                            titleModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 10.dp),
                            buttonName = "실행",
                            buttonModifier = Modifier.wrapContentSize(),
                            clickEvent = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    val workInfos = workManager.getWorkInfosForUniqueWork("UniqueWork2")
                                    for (work in workInfos.get()){
                                        Log.d(tag, "workInfos work = $work")
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
