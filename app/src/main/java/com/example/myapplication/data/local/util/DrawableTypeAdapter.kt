package com.example.myapplication.data.local.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type


class DrawableTypeAdapter : JsonSerializer<Drawable>, JsonDeserializer<Drawable> {

    override fun serialize(
        src: Drawable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null) return JsonNull.INSTANCE
        val bitmap = (src as? BitmapDrawable)?.bitmap
            ?: return JsonNull.INSTANCE

        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        val encoded = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)

        return JsonPrimitive(encoded) // ✅ now it will always be a string
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Drawable {
        if (json == null || json.isJsonNull) {
            return ColorDrawable(Color.TRANSPARENT)
        }
        val encoded = json.asString // ✅ guaranteed to be a string now
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return BitmapDrawable(Resources.getSystem(), bitmap)
    }


}
