package com.example.myapplication.ui.main


import LauncherOverview
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.data.local.DatabaseProvider
import com.example.myapplication.data.repo.LauncherRepository


class LauncherActivity : ComponentActivity() {

    private val vm: LauncherViewModel by viewModels {
        AppViewModelFactory(LauncherRepository(DatabaseProvider.getDatabase(this).launcherItemDao()))
    }


override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
LauncherOverview(viewModel = vm)
}


// Optional: handle deep intents
if (intent?.action == Intent.ACTION_MAIN) {
// launched as HOME — nothing extra to do
}
}
}