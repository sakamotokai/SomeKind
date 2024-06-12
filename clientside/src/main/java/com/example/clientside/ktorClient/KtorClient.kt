package com.example.clientside.ktorClient

import android.accessibilityservice.GestureDescription
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
import android.view.accessibility.AccessibilityEvent
import com.example.clientside.accessibilityService.HandleAccessibilityService
import com.example.clientside.accessibilityService.HandleAccessibilityService.Companion.gesture
import com.example.clientside.accessibilityService.HandleAccessibilityService.Companion.gestureCompleted
import com.google.gson.Gson
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close

class WebSocketClient(
    private val serverIp: String,
    private val serverPort: Int,
    private val accessibilityService: HandleAccessibilityService
) {
    //ksp-2.0.0-1.0.22 is too new for kotlin-1.9.0. Please upgrade kotlin-gradle-plugin to 2.0.0.
    private val client = HttpClient(OkHttp) {
        install(WebSockets) { contentConverter = KotlinxWebsocketSerializationConverter(Json) }
    }

    private var socketSession: DefaultClientWebSocketSession? = null

    private var _serverData: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    var serverData: StateFlow<List<String>> = _serverData

    private var _gestureAccess: MutableStateFlow<Path> =
        MutableStateFlow(Path())
    var gestureAccess: StateFlow<Path> = _gestureAccess

    var coroutineJob: Job? = null

    fun launch() {
        coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.webSocket(host = serverIp, port = serverPort, path = "/ws") {
                    socketSession = this

                    Log.e("localError", "inside init")

                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text ?: continue
                        val data_type: Array<PathDC> = arrayOf()
                        val arrayPathDC =
                            Gson().fromJson(othersMessage.readText(), data_type::class.java)
                        var done = false
                        launch {
                            arrayPathDC.forEach {
                                Log.e("localError", "PathDC: ${it.toString()}")
                            }
                        }
                        val waitedJob = launch {
                            performGesture(arrayPathDC) { done = it }
                        }
                        delay(500)//work imitation
                        waitedJob.join()
                        _serverData.value += othersMessage.readText() ?: "Nothing again"
                        Log.e("localError", othersMessage.readText() ?: "Nothing")
                        send(Frame.Text(done.toString()))
                    }
                }
            } catch (e: Exception) {
                Log.e("localError", "Client Exception: ${e.localizedMessage}")
            } finally {
                this@WebSocketClient.closeSession()
                socketSession = null
                coroutineJob = null
                this.cancel()
            }
        }
    }

    fun Array<com.example.clientside.ktorClient.dataset.PathDC>.convertToPath(): Array<Path> {
        var pathList: Array<Path> = arrayOf()
        this.forEach {
            pathList += Path().apply {
                moveTo(it.moveX, it.moveY)
                lineTo(it.lineX, it.lineY)
            }
        }
        return pathList
    }

    private suspend fun performGesture(gestures: Array<PathDC>, completed: (Boolean) -> Unit) {
        val dragDuration = 500L
        val convertedGestures = gestures.convertToPath()
        var startTime = 0L

        GestureDescription.Builder().apply {
            for (i in 0 until convertedGestures.size - 1) {
                val strokeDesk =
                    GestureDescription.StrokeDescription(
                        convertedGestures[i],
                        startTime,
                        dragDuration,
                        true
                    )
                this.addStroke(strokeDesk)
                startTime += dragDuration
            }
        }.addStroke(
            GestureDescription.StrokeDescription(
                convertedGestures.last(),
                startTime,
                dragDuration,
                false
            )
        ).build().also {
            accessibilityService.apply {
                gesture = it
                HandleAccessibilityService.service?.onAccessibilityEvent(AccessibilityEvent.obtain())
                //onAccessibilityEvent(null)
                completed(gestureCompleted)
            }
        }
    }

    fun connectionIsActive() = coroutineJob != null


    fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socketSession?.send(Frame.Text(message))
        }
    }

    fun closeClient() {
        client.close()
    }

    fun closeSession() {
        CoroutineScope(Dispatchers.IO).launch {
            socketSession?.close()
        }
    }

    fun sendAndClose(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            socketSession?.apply {
                send(Frame.Text(message))
                close()
            }
        }
    }
}