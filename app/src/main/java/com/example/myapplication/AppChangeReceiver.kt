package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppChangeReceiver(private val onAppChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_PACKAGE_ADDED ||
            intent?.action == Intent.ACTION_PACKAGE_REMOVED
        ) {
            onAppChanged()
        }
    }
}