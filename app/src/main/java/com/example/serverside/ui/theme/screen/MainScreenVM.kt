package com.example.serverside.ui.theme.screen

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serverside.server.WebSocketServer
import io.ktor.server.engine.launchOnCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainScreenVM : ViewModel() {

    private var _serverData: MutableStateFlow<Pair<String, Int>> =
        MutableStateFlow(Pair("192.168.1.211", 8080))
    var serverData: StateFlow<Pair<String, Int>> = _serverData


    private var server: WebSocketServer? = null

    private var startServerJob: Job? = null

    fun getIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        return ipAddress
    }

    fun setServerProperties(host: String, port: Int) {
        Log.e("localError", "Properties is setting")
        _serverData.value = Pair(host, port)
        if (server?.isRunning == true) {
            closeServer()
            Log.e("localError", "Server is closed")
        }
    }

    fun startServer() {
        Log.e("localError", "Server is starting ${server?.isRunning}")
        if (server?.isRunning == true) {
            Log.e("localError", "Server is already running")
            return
        }

        server = WebSocketServer(serverData.value.first, serverData.value.second)
        startServerJob = viewModelScope.launch(Dispatchers.IO) {
            server?.startServer()
        }
        Log.e("localError", server?.isRunning?.toString()?:"Not Running as such ju")
    }

    fun closeServer() {
        Log.e("localError", "Server is closing")
        if (server?.isRunning == true)
            viewModelScope.launch {
                server?.stopServer()
                startServerJob?.cancel()
            }
    }
}