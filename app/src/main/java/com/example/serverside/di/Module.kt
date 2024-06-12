package com.example.serverside.di

import androidx.room.Room
import com.example.serverside.roomdb.AppDatabase
import com.example.serverside.ui.theme.screen.MainScreenVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val koinModule = module {
    viewModel {
        MainScreenVM()
    }
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java, "base_1"
        ).build()
    }
}