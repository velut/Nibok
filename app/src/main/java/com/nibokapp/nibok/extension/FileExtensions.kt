package com.nibokapp.nibok.extension

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.nibokapp.nibok.ui.App
import id.zelory.compressor.Compressor
import id.zelory.compressor.FileUtil
import java.io.File
import java.io.IOException
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

/**
 * Compress the image located at the given file Uri.
 *
 * @param fileUri the uri of the image file
 * @param context the context. Defaults to the App context
 *
 * @return a File with the compressed image or null if the image could not be compressed
 *
 */
fun compressImage(fileUri: Uri, context: Context = App.instance): File? {
    val rawImageFile = try {
        FileUtil.from(context, fileUri) // Use FileUtil from Compressor library
    } catch (e: IOException) {
        Log.e(TAG, "Could not get file from uri: $fileUri")
        e.printStackTrace()
        return null
    }
    Log.d(TAG, "Compressing image")
    val compressedImageFile = try {
        Compressor.getDefault(context).compressToFile(rawImageFile)
    } catch (e: Exception) {
        Log.e(TAG, "Could not compress image")
        e.printStackTrace()
        return null
    }
    Log.d(TAG, "Compressed image successfully")
    return compressedImageFile
}