package com.example.clientside.ktorClient

import android.accessibilityservice.GestureDescription
import android.content.Context
import com.example.clientside.ktorClient.dataset.PathDC
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.example.clientside.accessibilityService.HandleAccessibilityService
import com.example.clientside.accessibilityService.HandleAccessibilityService.Companion.gesture
import com.example.clientside.accessibilityService.HandleAccessibilityService.Companion.gestureCompleted
import com.google.gson.Gson
import io.ktor.websocket.close

class WebSocketClient(
    private val accessibilityService: HandleAccessibilityService,
    private val context: Context
) {

    companion object {
        private var serverIp: String = "192.168.1.211"
        private var serverPort: Int = 8080
        private val client = HttpClient(OkHttp) {
            install(WebSockets) { contentConverter = KotlinxWebsocketSerializationConverter(Json) }
        }
    }

    private var socketSession: DefaultClientWebSocketSession? = null

    var coroutineJob: Job? = null

    fun launch() {

        coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.webSocket(host = serverIp ?: "127.0.0.1", port = serverPort, path = "/ws") {
                    socketSession = this

                    while (true) {
                        val othersMessage = incoming.receive() as? Frame.Text ?: continue
                        val data_type: Array<PathDC> = arrayOf()
                        val arrayPathDC =
                            Gson().fromJson(othersMessage.readText(), data_type::class.java)
                        var done = false
                        launch {
                            arrayPathDC.forEach {
                            }
                        }
                        val waitedJob = launch {
                            performGesture(arrayPathDC) {
                                done = it
                            }
                        }
                        delay(500)
                        waitedJob.join()
                        send(Frame.Text(done.toString()))
                    }
                }
            } catch (e: Exception) {
            } finally {
                this@WebSocketClient.closeSession()
                socketSession = null
                coroutineJob = null
                this.cancel()
            }
        }
    }

    fun changeWebSocketParameters(host: String, port: Int) {
        try {
            if (socketSession?.isActive != false) closeSession()
            if (coroutineJob?.isActive != false) coroutineJob?.cancel()
        } catch (e: Exception) {
        } finally {
            serverIp = host
            serverPort = port
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