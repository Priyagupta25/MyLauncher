package com.example.myapplication.data.local.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.util.Base64
import androidx.room.TypeConverter
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.data.local.entity.LauncherItemType
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class Converters {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Drawable::class.java, DrawableTypeAdapter())
        .create()

    @TypeConverter
    fun fromType(type: LauncherItemType): String = type.name

    @TypeConverter
    fun toType(value: String): LauncherItemType = LauncherItemType.valueOf(value)


    // UserHandle
    @TypeConverter
    fun fromUserHandle(user: UserHandle?): String? = user?.toString()

    @TypeConverter
    fun toUserHandle(value: String?): UserHandle? {
        // Note: There’s no public API to recreate arbitrary UserHandle
        // Usually you just store "owner" vs "work profile"
        return null
    }

    // Drawable <-> Base64
    @TypeConverter
    fun fromDrawable(drawable: Drawable?): String? {
        if (drawable == null) return null
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @TypeConverter
    fun toDrawable(base64: String?): Drawable? {
        // ⚠️ Needs context to convert back into Drawable
        return null
    }

    // Intent
    @TypeConverter
    fun fromIntent(intent: Intent?): String? =
        intent?.toUri(Intent.URI_INTENT_SCHEME)

    @TypeConverter
    fun toIntent(value: String?): Intent? =
        value?.let { Intent.parseUri(it, 0) }



    // MutableList<AppInfo> -> JSON
    @TypeConverter
    fun fromAppInfoList(list: MutableList<AppInfo>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toAppInfoList(value: String?): MutableList<AppInfo>? {
        if (value == null) return null
        val type = object : TypeToken<MutableList<AppInfo>>() {}.type
        return gson.fromJson(value, type)
    }
}