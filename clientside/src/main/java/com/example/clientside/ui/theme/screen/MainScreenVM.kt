package com.example.clientside.ui.theme.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.example.clientside.ktorClient.WebSocketClient

class MainScreenVM(private val webSocketClient: WebSocketClient) : ViewModel() {
    fun closeOrOpenConnectionWebSocket() {
        if (webSocketClient.connectionIsActive()) webSocketClient.closeSession()
        else webSocketClient.launch()
    }

    fun changeWebSocketParameters(host: String, port: Int) {
        webSocketClient.changeWebSocketParameters(host, port)
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

    fun openAccessibilityOSScreen(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

}