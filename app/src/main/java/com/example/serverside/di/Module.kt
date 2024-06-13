package com.example.serverside.di


import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.serverside.db.sqlite.GestureDbHelper
import com.example.serverside.repository.DbRepository
import com.example.serverside.repository.DbRepositoryImpl
import com.example.serverside.server.WebSocketServer
import com.example.serverside.ui.theme.screen.LogsScreenVM
import com.example.serverside.ui.theme.screen.MainScreenVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val koinModule = module {

    viewModelOf(::MainScreenVM)
    viewModelOf(::LogsScreenVM)

    single {
        GestureDbHelper(get())
    }

    single<DbRepository> {
        DbRepositoryImpl(get())
    }

    single {
        val context: Context = get()
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        WebSocketServer(get()).apply {
            this.setProperty(8080, ipAddress)
        }
    }
}