package com.example.clientside.ktorClient

import android.util.Log
import androidx.compose.runtime.MutableState
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WebSocketClient(private val serverIp: String, private val serverPort: Int) {

    private val client = HttpClient(OkHttp) {
        install(WebSockets)
    }

    private lateinit var socketSession: DefaultClientWebSocketSession

    private var _serverData: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    var serverData: StateFlow<List<String>> = _serverData

    init {
        CoroutineScope(Dispatchers.IO).launch {
            client.webSocket(host = serverIp, port = serverPort, path = "/ws") {
                socketSession = this
                Log.e("localError", "inside init")
                send(Frame.Text("Hello from client!"))

                while(true) {
                    val othersMessage = incoming.receive() as? Frame.Text
                    _serverData.value += othersMessage?.readText()?:"Nothing again"
                }
            }
        }
    }


    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socketSession.send(Frame.Text(message))
        }
    }

    fun closeClient(){
        client.close()
    }
}