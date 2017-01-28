package com.nibokapp.nibok.extension

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Collection of extensions related to File operations.
 */

val TAG = "FileExtensions"


/**
 * Try to create an unique image file in which to store a picture.
 *
 * @param context the context used to retrieve the external file directory
 * @param inCacheDir true if the file has to be created in the cache directory. Default is false
 *
 * @return a File if the file was created successfully,
 * null if no file could be created
 */
fun createImageFile(context: Context, inCacheDir: Boolean = false): File? {
    @SuppressLint("SimpleDateFormat")
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timestamp"
    val extension = ".jpg"

    val storageDir = if (inCacheDir) {
        context.cacheDir
    } else {
        // Get the external public files directory
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    val imageFile: File? = try {
        File.createTempFile(imageFileName, extension, storageDir)
    } catch (e: IOException) {
        Log.d(TAG, "Could not create image file\nException: $e")
        null
    }

    return imageFile
}

fun Uri.toInputStream(context: Context): InputStream? {
    return try {
        context.contentResolver.openInputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "Could not find file associated to: $this")
        e.printStackTrace()
        null
    }
}

fun InputStream.toFile(outFilePath: String): File? {
    val inputStream = this as? FileInputStream ?: return null
    var outputStream: FileOutputStream? = null
    try {
        val tmpFile = File(outFilePath)
        outputStream = FileOutputStream(tmpFile)

        val bytes = ByteArray(1024)

        while (true) {
            val read = inputStream.read(bytes)
            if (read == -1) break
            outputStream.write(bytes, 0, read)
        }
        Log.d(TAG, "Copied input stream to file")
        return tmpFile
    } catch (e: Exception) {
        Log.e(TAG, "Error while copying to file")
        e.printStackTrace()
        return null
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error while closing input stream")
            e.printStackTrace()
        }

        try {
            outputStream?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error while closing output stream")
            e.printStackTrace()
        }
    }
}
