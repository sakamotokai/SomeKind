package com.example.serverside

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.serverside.db.sqlite.GestureDbHelper
import com.example.serverside.navigation.Navigation
import com.example.serverside.ui.theme.ServerSideTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ServerSideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Navigation(navController = rememberNavController())
                }
            }
        }
    }

    override fun onDestroy() {
        inject<GestureDbHelper>().apply {
            this.value.close()
        }
        super.onDestroy()
    }
}