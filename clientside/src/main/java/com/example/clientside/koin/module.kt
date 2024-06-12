package com.example.clientside.koin

import com.example.clientside.ktorClient.WebSocketClient
import org.koin.dsl.module

val koinModule = module {
    single {
        WebSocketClient("192.168.1.211", 8080)
    }
}