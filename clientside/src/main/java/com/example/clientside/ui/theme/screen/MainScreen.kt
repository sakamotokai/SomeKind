package com.example.clientside.ui.theme.screen

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
import com.example.clientside.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(context: Context) {
    var extendPortConfig by remember { mutableStateOf(false) }
    val mainScreenVM = koinViewModel<MainScreenVM>()
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            extendPortConfig = !extendPortConfig
        }) {
            Text(text = context.getString(R.string.configuration))
        }
        if (extendPortConfig) {
            ExtendedConfig(
                mainScreenVM = mainScreenVM,
                context = context,
                switch = { extendPortConfig = !extendPortConfig }
            )
        }
        Button(onClick = {
            mainScreenVM.closeOrOpenConnectionWebSocket()
        }) {
            Text(text = context.getString(R.string.start_stop_button))
        }

        Button(onClick = {
            mainScreenVM.openAccessibilityOSScreen(context)
        }) {
            Text(text = context.getString(R.string.open_accessibility_screen))
        }
    }
}

@Composable
private fun ExtendedConfig(
    mainScreenVM: MainScreenVM,
    switch: () -> Unit,
    context: Context
) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableIntStateOf(8080) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = host, onValueChange = { host = it }, placeholder = {
            Text(text = context.getString(R.string.host))
        })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = port.toString(),
            onValueChange = {
                port = mainScreenVM.checkForInteger(it)
            },
            placeholder = {
                Text(text = context.getString(R.string.port))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            mainScreenVM.changeWebSocketParameters(host, port)
            switch()
        }) {
            Text(text = context.getString(R.string.accept))
        }
    }
}
