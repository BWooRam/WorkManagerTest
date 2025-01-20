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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

                        
                    }
                }
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