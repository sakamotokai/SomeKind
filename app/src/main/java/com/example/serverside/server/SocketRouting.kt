package com.example.serverside.server

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.server.engine.embeddedServer
import io.ktor.server.application.*
import io.ktor.server.engine.stop
import io.ktor.server.jetty.Jetty
import io.ktor.server.jetty.JettyApplicationEngine
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration


class WebSocketServer(private var host: String = "127.0.0.1", private var port: Int = 8080) {

    private var _serverData: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    var serverData: StateFlow<List<String>> = _serverData

    var isRunning by mutableStateOf(false)

    private var sessions: HashSet<DefaultWebSocketSession> = HashSet()
    private var server: JettyApplicationEngine? = null

    fun getServerInstance() = server

    suspend fun startServer() {
        Log.e("localError", "Start Server")

        Log.e("localError", "WebSocketServer init {$host} $port")

        embeddedServer(Jetty, port = port, host = host) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing {
                webSocket("/ws") {
                    sessions += this
                    try {
                        send("You are connected to WebSocket server!")
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val receivedText = frame.readText()
                                sessions.forEach {
                                    it.send(Frame.Text("You said: $receivedText"))
                                    _serverData.value += receivedText
                                }
                            }
                        }
                    } catch (_: Exception) {
                    } finally {
                        sessions -= this
                    }
                }
            }
        }.apply {
            try {
                server = this
                isRunning = true
                this.start(wait = true)
            } catch (e: Exception) {
                Log.e("localError", "EXCEPTION: ${e.localizedMessage ?: ""}")
                isRunning = false
            }
        }
    }


    suspend fun stopServer() {
        Log.e("localError", "Stop Server")
        try {
            server?.stop()
            isRunning = false
        } catch (_: Exception) {
            isRunning = true
        }
    }
}