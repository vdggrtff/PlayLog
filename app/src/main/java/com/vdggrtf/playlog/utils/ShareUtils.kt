package com.vdggrtf.playlog.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    fun shareImage(context: Context, bitmap: Bitmap) {
        try {
            Log.d("SHARE_DEBUG", "1. Начало сохранения картинки...")
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "gamer_passport.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            Log.d("SHARE_DEBUG", "2. Картинка сохранена в кэш: ${file.absolutePath}")

            val authority = "${context.packageName}.provider"
            Log.d("SHARE_DEBUG", "3. Пытаемся получить URI. Authority: $authority")

            val imageUri = FileProvider.getUriForFile(context, authority, file)
            Log.d("SHARE_DEBUG", "4. URI успешно получен: $imageUri")

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out my Gamer License in PlayLog! Can you beat my stats? \uD83C\uDFAE"
                )

                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                clipData = ClipData.newRawUri("", imageUri)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Gamer ID"))
            Log.d("SHARE_DEBUG", "5. Системное окно Share успешно вызвано!")

        } catch (e: Exception) {
            Log.e("SHARE_DEBUG", "❌ ФАТАЛЬНАЯ ОШИБКА ШАРИНГА: ${e.stackTraceToString()}")
        }
    }
}