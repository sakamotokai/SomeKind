package com.example.clientside.ktorClient

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.clientside.ktorClient.dataset.PathDC
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import android.graphics.Path
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close

class WebSocketClient(private val serverIp: String, private val serverPort: Int) {

    private val client = HttpClient(OkHttp) {
        install(WebSockets){contentConverter = KotlinxWebsocketSerializationConverter(Json) }
    }

    private lateinit var socketSession: DefaultClientWebSocketSession

    private var _serverData: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    var serverData: StateFlow<List<String>> = _serverData

    private var _gestureAccess: MutableStateFlow<Path> =
        MutableStateFlow(Path())
    var gestureAccess: StateFlow<Path> = _gestureAccess

    var coroutineJob:Job? = null

    fun launch(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.webSocket(host = serverIp, port = serverPort, path = "/ws") {
                    socketSession = this

                    Log.e("localError", "inside init")

                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text
                        delay(1000)//work imitation
                        _serverData.value += othersMessage?.readText() ?: "Nothing again"
                        Log.e("localError",othersMessage?.readText()?:"Nothing")
                        send(Frame.Text("done"))
                    }
                }
            } catch (_:Exception){

            } finally {
                this@WebSocketClient.closeSession()
                this.cancel()
            }
        }.also {
            coroutineJob = it
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

    fun closeSession(){
        CoroutineScope(Dispatchers.IO).launch {
            socketSession.close()
        }
    }

    fun sendAndClose(message:String){
        CoroutineScope(Dispatchers.IO).launch {
            socketSession.apply {
                send(Frame.Text(message))
                close()
            }
        }
    }
}