package com.example.serverside.di

import com.example.serverside.ui.theme.screen.MainScreenVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val koinModule = module {
    viewModel {
        MainScreenVM()
    }
}