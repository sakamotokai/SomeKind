package com.example.clientside.koin

import com.example.clientside.accessibilityService.HandleAccessibilityService
import com.example.clientside.ktorClient.WebSocketClient
import com.example.clientside.ui.theme.screen.MainScreenVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val koinModule = module {
    viewModelOf(::MainScreenVM)
    single {
        WebSocketClient(get(),get())
    }
    singleOf(::HandleAccessibilityService)
}