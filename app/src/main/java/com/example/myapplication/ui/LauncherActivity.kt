package com.example.myapplication.ui


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.AppChangeReceiver
import com.example.myapplication.ui.compose.HomeScreen
import com.example.myapplication.ui.main.LauncherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {
    private lateinit var receiver: AppChangeReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LauncherViewModel = hiltViewModel()

            // Register receiver
            LaunchedEffect(Unit) {
                receiver = AppChangeReceiver {
                    viewModel.loadInstalledApps()
                }
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addAction(Intent.ACTION_PACKAGE_REMOVED)
                    addDataScheme("package")
                }
                registerReceiver(receiver, filter)

                viewModel.loadInstalledApps()
            }

            HomeScreen(viewModel)
        }
    }
}