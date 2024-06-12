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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import    android.graphics.Path
import com.example.serverside.server.dataset.PathDC
import com.example.serverside.server.dataset.pathDataSet
import com.google.gson.Gson
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import org.json.JSONObject


class WebSocketServer(private var host: String = "127.0.0.1", private var port: Int = 8080) {

    private var _serverData: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    var serverData: StateFlow<List<String>> = _serverData

    var isRunning by mutableStateOf(false)

    private var sessions: HashSet<DefaultWebSocketSession> = HashSet()
    private var server: JettyApplicationEngine? = null

    companion object {
        private var coroutineSessions: MutableList<Job?> = mutableListOf(null)
    }

    private val scope = CoroutineScope(SupervisorJob())

    fun getServerInstance() = server

    suspend fun startServer() {
        Log.e("localError", "Start Server")

        Log.e("localError", "WebSocketServer init {$host} $port")

        embeddedServer(Jetty, port = port, host = host) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }//TODO("SEND JSON")
            routing {
                webSocket("/ws") {
                    Log.e("localError", "WebSocket is Activated")
                    coroutineSessions += scope.launch(Dispatchers.IO) {
                        sessions += this@webSocket
                        val gson = Gson()
                        val json = gson.toJson(pathDataSet)
                        try {
                            while (true) {
                                send(Frame.Text(json))
                                val nestedScope = launch {
                                    while (true) {
                                        for (frame in incoming) {
                                            frame as? Frame.Text ?: continue
                                            Log.e("localError", "Write to RoomDB")
                                            this.cancel()
                                        }
                                    }
                                }
                                delay(3000)
                                if (!nestedScope.isCancelled) {
                                    nestedScope.cancel()
                                    for (frame in incoming) {
                                        frame as? Frame.Text ?: continue
                                        Log.e("localError", "Write to RoomDB2")
                                    }
                                }

                                /*                            send()
                                                        for(frame in incoming){
                                                            frame as? Frame.Text?:continue
                                                            if(frame.readText() == "start"){

                                                            } else if(frame.readText() == "done"){

                                                            }
                                                        }*/

                            }
                        } finally {
                            sessions -= this@webSocket
                            this.cancel()
                        }
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