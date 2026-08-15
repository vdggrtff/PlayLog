package com.vdggrtf.playlog.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempPictureUri(): Uri {
    // Create directory in cache, if he didn't
    val imagePath = File(cacheDir, "images").apply { mkdirs() }
    // Create temporary file
    val file = File(imagePath, "bounty_proof_${System.currentTimeMillis()}.jpg")

    // Return safe Uri for FileProvider
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        file
    )
}