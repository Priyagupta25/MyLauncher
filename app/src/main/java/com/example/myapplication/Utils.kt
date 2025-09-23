package com.example.myapplication

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.net.Uri
import android.os.UserHandle
import android.provider.Settings
import android.util.Log
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.entity.LauncherItemEntity
import com.example.myapplication.data.local.entity.LauncherItemType

object Utils {
    fun launchApp(context: Context, packageName: String) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { context.startActivity(it) }
    }

    fun openAppInfo(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun uninstallApp(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun disableApp(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        context.startActivity(intent)
    }


    fun canUninstall(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)

            val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                    (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val isProtected =
                dpm.isDeviceOwnerApp(packageName) || dpm.isProfileOwnerApp(packageName)

            !isSystemApp && !isProtected
        } catch (e: Exception) {
            false
        }
    }

    fun hasAppWidgets(context: Context, packageName: String): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providers = appWidgetManager.installedProviders
        return providers.any { it.provider.packageName == packageName }
    }

    fun openWidgetPicker(context: Context, packageName: String) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providers = appWidgetManager.installedProviders
            .filter { it.provider.packageName == packageName }

        if (providers.isNotEmpty()) {

            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
            intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            intent.putParcelableArrayListExtra(
                AppWidgetManager.EXTRA_CUSTOM_INFO,
                ArrayList() // empty custom widgets
            )
            intent.putParcelableArrayListExtra(
                AppWidgetManager.EXTRA_CUSTOM_EXTRAS,
                ArrayList()
            )

            context.startActivity(intent)
        }
    }

    @SuppressLint("NewApi")
    fun hasShortcuts(context: Context, packageName: String, user: UserHandle): Boolean {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        if (!launcherApps.hasShortcutHostPermission()) return false
        val query = LauncherApps.ShortcutQuery().setQueryFlags(
            LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
        ).setPackage(packageName)
        val shortcuts = launcherApps.getShortcuts(query, user)
        return !shortcuts.isNullOrEmpty()
    }

    @SuppressLint("NewApi")
    fun getAppShortcuts(
        context: Context,
        packageName: String,
        userHandle: UserHandle
    ): List<ShortcutInfo> {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        return launcherApps.getShortcuts(
            LauncherApps.ShortcutQuery().setQueryFlags(
                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                        LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED or
                        LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST
            ).setPackage(packageName),
            userHandle
        ) ?: emptyList()
    }


    fun LauncherItemEntity.toDomain(): LauncherItem {
        Log.d("Priya", "appInfo $apps")
        Log.d("Priya", "label $label")
        return when (type) {
            LauncherItemType.APP -> LauncherItem.App(
                appInfo = apps.get(0)
            )

            LauncherItemType.FOLDER -> LauncherItem.Folder(
                name = label,
                apps = apps
            )
        }
    }

    fun LauncherItem.toEntity(): LauncherItemEntity =
        when (this) {
            is LauncherItem.App -> LauncherItemEntity(
                id =  mutableListOf(appInfo.packageName).hashCode().toLong(),
                apps = mutableListOf(appInfo),
                label = "",
                type = LauncherItemType.APP
            )

            is LauncherItem.Folder -> LauncherItemEntity(
                id=  apps.toMutableList().hashCode().toLong(),
                type = LauncherItemType.FOLDER,
                label = name,
                apps = apps.toMutableList()
            )
        }

}