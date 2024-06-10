package com.example.clientside

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import java.util.Scanner

class ClientSide : ComponentActivity() {

    lateinit var webSocketClient: WebSocketClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serverIp = "192.168.1.211"
        val serverPort = 8080

        webSocketClient = WebSocketClient(serverIp, serverPort)

        enableEdgeToEdge()
        setContent {
            ServerSideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        client = webSocketClient,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(client: WebSocketClient, modifier: Modifier) {
    val listCompose = client.serverData.collectAsState()
    LazyColumn {
        items(listCompose.value) {
            Text(text = it)
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            var text by remember { mutableStateOf("") }
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                OutlinedTextField(value = text, onValueChange = { text = it })
                Button(onClick = { client.sendMessage(text);text = "" }) {
                    Text(text = "Send")
                }
            }
        }
    }
}
