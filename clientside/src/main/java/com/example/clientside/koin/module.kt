package com.example.clientside.koin

import com.example.clientside.accessibilityService.HandleAccessibilityService
import com.example.clientside.ktorClient.WebSocketClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinModule = module {
    single {
        WebSocketClient("192.168.1.211", 8080,get())
    }
    singleOf(::HandleAccessibilityService)
}