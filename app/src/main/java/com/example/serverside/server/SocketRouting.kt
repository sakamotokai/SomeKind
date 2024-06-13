package com.example.serverside.server

import android.content.ContentValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.server.engine.embeddedServer
import io.ktor.server.application.*
import io.ktor.server.jetty.Jetty
import io.ktor.server.jetty.JettyApplicationEngine
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Duration
import com.example.serverside.db.sqlite.DBContract
import com.example.serverside.repository.DbRepository
import com.example.serverside.server.dataset.pathDataSet
import com.google.gson.Gson
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json


class WebSocketServer(
    private val db: DbRepository
) {

    companion object {
        private var clientHost: String = "127.0.0.1"
        private var clientPort: Int = 8080
    }

    var isRunning by mutableStateOf(false)

    private var sessions: HashSet<DefaultWebSocketSession> = HashSet()
    private var server: JettyApplicationEngine? = null

    suspend fun startServer() {

        embeddedServer(Jetty, port = clientPort, host = clientHost) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing {
                val coroutineScope = CoroutineScope(SupervisorJob())
                coroutineScope.launch {
                    webSocket("/ws") {
                        sessions += this

                        val dataset = Gson().toJson(pathDataSet)

                        try {
                            send(Frame.Text(dataset))
                            while (true) {
                                for (frame in incoming) {
                                    if (frame as? Frame.Text == null) continue

                                    launch {
                                        try {
                                            val frameData = frame.readText()
                                            pathDataSet.forEach {
                                                ContentValues().apply {
                                                    put(
                                                        DBContract.Gesture.COLUMN_NAME_MOVETOX,
                                                        it.moveX
                                                    )
                                                    put(
                                                        DBContract.Gesture.COLUMN_NAME_MOVETOY,
                                                        it.moveY
                                                    )
                                                    put(
                                                        DBContract.Gesture.COLUMN_NAME_LINETOX,
                                                        it.lineX
                                                    )
                                                    put(
                                                        DBContract.Gesture.COLUMN_NAME_LINETOY,
                                                        it.lineY
                                                    )
                                                    put(
                                                        DBContract.Gesture.COLUMN_NAME_DONE,
                                                        frameData
                                                    )
                                                }.apply {
                                                    db.insert(
                                                        DBContract.Gesture.TABLE_NAME,
                                                        this
                                                    )
                                                }
                                            }
                                        } catch (e: Exception) {

                                        } finally {
                                            this.cancel()
                                        }
                                    }
                                    send(Frame.Text(dataset))
                                }
                                delay(500)
                            }
                        } catch (e: Exception) {
                            sessions -= this@webSocket
                            this@webSocket.close()
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
                isRunning = false
            }
        }
    }

    fun setProperty(port: Int, host: String) {
        try {
            if (isRunning) server?.stop()
            clientHost = host
            clientPort = port
        } catch (e:Exception){}
    }

    suspend fun stopServer() {
        try {
            server?.stop()
            isRunning = false
        } catch (_: Exception) {

        }
    }
}
