package com.nibokapp.nibok.util

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import com.nibokapp.nibok.extension.createImageFile
import com.nibokapp.nibok.extension.toFile
import com.nibokapp.nibok.extension.toInputStream
import com.nibokapp.nibok.ui.App
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility object for compressing image files.
 */
object ImageCompressor {

    private val TAG = "ImageCompressor"

    /**
     * Compress the image located at the given file Uri.
     *
     * @param imageFileUri the uri of the image file
     * @param maxWidth maximum width for the compressed image. Default is 612px
     * @param maxHeight maximum height for the compressed image. Default is 816px
     * @param maxQuality maximum quality for the compressed image. Default is 80 (out of 100)
     * @param context the context. Defaults to the App context
     *
     * @return a File with the compressed image or null if the image could not be compressed
     *
     */
    fun compressImage(imageFileUri: Uri,
                      maxWidth: Float = 612.0f,
                      maxHeight: Float = 816.0f,
                      maxQuality: Int = 80,
                      context: Context = App.instance): File? {

        val imageFilePath = createImageFile(context, true)?.path ?: return null
        val imageFile = imageFileUri.toInputStream(context)?.toFile(imageFilePath) ?: return null
        Log.d(TAG, "Got image file at path: $imageFilePath")

        Log.d(TAG, "Checking image sizes")
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // Don't load actual bitmap pixels, bitmap will be null
        BitmapFactory.decodeFile(imageFilePath, options)
        val actualWidth = options.outWidth
        val actualHeight = options.outHeight

        if (actualWidth <= maxWidth && actualHeight <= maxHeight) {
            Log.d(TAG, "Image does not need processing")
            return imageFile
        }

        Log.d(TAG, "Processing image")
        val imageRatio = actualWidth / actualHeight
        val maxRatio = maxWidth / maxHeight

        val outRatio: Float
        val outWidth: Int
        val outHeight: Int

        if (imageRatio < maxRatio) {
            outRatio = maxHeight / actualHeight
            outWidth = (outRatio * actualWidth).toInt()
            outHeight = maxHeight.toInt()
        } else if (imageRatio > maxRatio) {
            outRatio = maxWidth / actualWidth
            outWidth = maxWidth.toInt()
            outHeight = (outRatio * actualHeight).toInt()
        } else {
            outWidth = maxWidth.toInt()
            outHeight = maxHeight.toInt()
        }

        options.apply {
            inSampleSize = calculateInSampleSize(options, outWidth, outHeight)
            inJustDecodeBounds = false // Fully decode the bitmap this time
            inTempStorage = ByteArray(16 * 1024)
        }

        val bitmap = try {
            BitmapFactory.decodeFile(imageFilePath, options)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        } ?: return null

        val tmpScaledBitmap = try {
            Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        } ?: return null

        val ratioX = outWidth / options.outWidth.toFloat()
        val ratioY = outHeight / options.outHeight.toFloat()
        val middleX = outWidth / 2.0f
        val middleY = outHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(tmpScaledBitmap)
        canvas.apply {
            matrix = scaleMatrix
            drawBitmap(bitmap, middleX - bitmap.width / 2, middleY - bitmap.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
        }

        val scaledBitmap = try {
            // Keep the image's orientation
            val exif = ExifInterface(imageFilePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            Log.d(TAG, "Exif orientation: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            Log.d(TAG, "Creating scaled bitmap")
            Bitmap.createBitmap(tmpScaledBitmap, 0, 0, tmpScaledBitmap.width, tmpScaledBitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } ?: return null

        val compressedImageFile = createImageFile(context, true) ?: return null

        val compressed = try {
            val outputStream = FileOutputStream(compressedImageFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, maxQuality, outputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }

        return if (compressed) compressedImageFile else null
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, requiredWidth: Int, requiredHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > requiredWidth || height > requiredHeight) {
            val widthRatio = Math.round(width.toFloat() / requiredWidth.toFloat())
            val heightRatio = Math.round(height.toFloat() / requiredHeight.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        val totalPixels = width * height
        val totalRequiredPixelsCap = requiredWidth * requiredHeight * 2
        while ((totalPixels / (inSampleSize * inSampleSize)) > totalRequiredPixelsCap) {
            inSampleSize += 1
        }

        return inSampleSize
    }
}