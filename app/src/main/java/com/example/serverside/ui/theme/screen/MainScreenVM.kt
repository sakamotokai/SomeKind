package com.example.serverside.ui.theme.screen

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serverside.repository.DbRepository
import com.example.serverside.server.WebSocketServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainScreenVM(private val db:DbRepository, private val server: WebSocketServer) : ViewModel() {

    private var startServerJob: Job? = null


    fun getIP(context: Context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        return ipAddress
    }

    fun setServerProperties(host: String, port: Int) {
        if (server.isRunning) {
            closeServer()
        }
        server.setProperty(port, host)
    }

    fun startServer() {
        if (server?.isRunning == true) {
            return
        }
        startServerJob = viewModelScope.launch(Dispatchers.IO) {
            server?.startServer()
        }
    }

    fun closeServer() {
        if (server?.isRunning == true)
            viewModelScope.launch {
                server?.stopServer()
                startServerJob?.cancel()
            }
    }

    fun checkForInteger(value: String): Int {
        var newValue = ""
        value.forEach { symbol ->
            if (symbol in '0'..'9') {
                newValue += symbol
            }
        }
        return try {
            newValue.toInt()
        } catch (_: Exception) {
            value.toInt()
        }

    }
}