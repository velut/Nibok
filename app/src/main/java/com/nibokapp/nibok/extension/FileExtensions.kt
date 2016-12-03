package com.nibokapp.nibok.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
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
 *
 * @return a File if the file was created successfully,
 * null if no file could be created
 */
fun createImageFile(context: Context): File? {
    @SuppressLint("SimpleDateFormat")
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timestamp"
    val extension = ".jpg"

    // Get the external public files directory
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val imageFile: File? =
            try {
                File.createTempFile(imageFileName, extension, storageDir)
            } catch (e: IOException) {
                Log.d(TAG, "Could not create image file\nException: $e")
                null
            }

    return imageFile
}