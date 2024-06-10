package com.example.serverside.ui.theme.screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.serverside.navigation.NavigationItem
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen(navController: NavHostController) {
    val mainScreenVM = koinViewModel<MainScreenVM>()
    var extendPortConfig by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { extendPortConfig = !extendPortConfig }) {
            Text(text = "Config")
        }
        if (extendPortConfig) {
            ExtendedConfig(
                switch = { extendPortConfig = false },
                setServerProperties = { host, port -> mainScreenVM.setServerProperties(host, port) }
            )
        }
        Button(onClick = { mainScreenVM.startServer() }) {
            Text(text = "Начать")
        }
        Button(onClick = { mainScreenVM.closeServer() }) {
            Text(text = "Выключить")
        }
        Button(onClick = { navController.navigate(NavigationItem.Logs.route) }) {
            Text(text = "Логи")
        }
        Text(text = mainScreenVM.getIP(koinInject<Context>()))
    }
}

@Composable
private fun ExtendedConfig(
    switch: () -> Unit,
    setServerProperties: (host: String, port: Int) -> Unit
) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableIntStateOf(8080) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = host, onValueChange = { host = it })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = port.toString(),
            onValueChange = {
                var value = ""
                it.forEach { symbol ->
                    if (symbol in '0'..'9') {
                        value += symbol
                    }
                }
                try {
                    port = value.toInt()
                } catch (_: Exception) {
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            setServerProperties(host, port)
            switch()
        }) {
            Text(text = "Принять")
        }
    }
}
