package com.example.clientside

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clientside.accessibilityService.HandleAccessibilityService
import com.example.clientside.ktorClient.WebSocketClient
import com.example.clientside.ui.theme.ServerSideTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import java.util.Scanner

class ClientSide : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ServerSideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(context = applicationContext)
                }
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val webSocketClient = koinInject<WebSocketClient>()
    var extendPortConfig by remember { mutableStateOf(false) }
    val serverIp = "192.168.1.211"
    val serverPort = 8080
    val service = koinInject<HandleAccessibilityService>()
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            try {
                HandleAccessibilityService().apply {
                    HandleAccessibilityService.handler.forEach {
                        it?.disableSelf()
                    }
                    HandleAccessibilityService.handler.clear()
                }
            } catch (e: Exception) {
                Log.e("localError", "Exception ${e.localizedMessage}")
            }
            extendPortConfig = !extendPortConfig
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }) {
            Text(text = "Config")
        }
        if (extendPortConfig) {
            ExtendedConfig(

            )
        }
        Button(onClick = {
            if (webSocketClient.connectionIsActive()) webSocketClient.closeSession()
            else webSocketClient.launch()//TODO("CARRY TO VIEWMODEL AND MAKE IT WORK")
        }) {
            Text(text = "Начать/Пауза")
        }

        Button(onClick = { val urlIntent = Intent(
            Settings.ACTION_ACCESSIBILITY_SETTINGS
        )
            context.startActivity(urlIntent) }) {
            Text(text = "Accept accessibility service")
        }
    }
}

@Composable
fun ExtendedConfig() {

}
