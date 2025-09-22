package com.example.myapplication.data.local.entity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.UserHandle

data class AppInfo(
    val packageName: String,
    val user: UserHandle,
    val label: String,
    var icon: Drawable ,
    val launchIntent: Intent? = null,
)