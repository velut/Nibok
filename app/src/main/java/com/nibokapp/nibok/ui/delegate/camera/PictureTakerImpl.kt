package com.nibokapp.nibok.ui.delegate.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.nibokapp.nibok.extension.createImageFile
import com.nibokapp.nibok.ui.delegate.camera.common.PictureTaker
import java.io.File

/**
 * Implementation of PictureTaker.
 */
class PictureTakerImpl() : PictureTaker {

    companion object {
        private val TAG = PictureTakerImpl::class.java.simpleName

        /**
         * Request code for picture taking.
         */
        const private val REQUEST_IMAGE_CAPTURE = 1

        /**
         * File provider authority constant.
         */
        const private val FILE_PROVIDER = "com.nibokapp.nibok.fileprovider"

        /**
         * Read and write permissions on a URI to be granted to the camera.
         */
        const private val URI_RW_PERMISSIONS =
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    private var currentPictureUri: Uri = Uri.EMPTY


    override fun getStartPictureTakingActivityArgs(context: Context): Pair<Intent, Int> {

        val pictureFile: File = createImageFile(context)
                ?: throw IllegalStateException("Cannot create a file to store the picture")

        currentPictureUri = FileProvider.getUriForFile(context, FILE_PROVIDER, pictureFile)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val pictureActivities = getDefaultPictureTakingActivities(context, takePictureIntent)
        if (pictureActivities.isEmpty()) {
            resetUri(context)
            throw IllegalStateException("No activities that can take a picture found")
        }

        grantUriReadWritePermissions(context, pictureActivities)

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPictureUri)
        Log.d(TAG, "Ready to start picture taking activity")
        return Pair(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onPictureTakingActivityResult(context: Context,
                                               requestCode: Int,
                                               resultCode: Int, data: Intent?): Uri? {
        revokeUriPermissions(context)
        return handleResult(context, requestCode, resultCode)
    }

    override fun setCurrentPictureUri(uri: Uri) {
        Log.d(TAG, "Setting picture Uri")
        currentPictureUri = uri
    }

    override fun getCurrentPictureUri(): Uri = currentPictureUri

    /**
     * Handle the result from the picture activity.
     */
    private fun handleResult(context: Context, requestCode: Int, resultCode: Int): Uri? {

        if (requestCode != REQUEST_IMAGE_CAPTURE) return null

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Picture successfully taken")
            return currentPictureUri
        } else {
            Log.d(TAG, "Image capture was unsuccessful, resetting Uri")
            resetUri(context)
            return null
        }
    }

    /**
     * Grant to each of the given activities the permission
     * to read and write to the current picture Uri.
     * If this is not done SecurityException is raised and the camera apps crash.
     */
    private fun grantUriReadWritePermissions(context: Context,
                                             pictureActivities: List<ResolveInfo>) {
        pictureActivities.forEach {
            val packageName = it.activityInfo.packageName
            Log.d(TAG, "Granting Uri permission to: $packageName")
            context.grantUriPermission(packageName, currentPictureUri, URI_RW_PERMISSIONS)
        }
    }

    /**
     * Revoke previously given permissions on the current Uri.
     */
    private fun revokeUriPermissions(context: Context) {
        if (currentPictureUri != Uri.EMPTY) {
            context.revokeUriPermission(currentPictureUri, URI_RW_PERMISSIONS)
        }
    }

    /**
     * Delete the unused file associated to the last Uri.
     * Reset the current Uri.
     */
    private fun resetUri(context: Context) {
        context.contentResolver.delete(currentPictureUri, null, null)
        currentPictureUri = Uri.EMPTY
    }

    /**
     * Get the default activities present in the phone able to take pictures.g
     *
     * @param takePictureIntent the intent used to resolve the activities
     *
     * @return a list of resolved activities that might be empty if no suitable activity was found
     */
    private fun getDefaultPictureTakingActivities(context: Context,
                                                  takePictureIntent: Intent): List<ResolveInfo> {
        return context.packageManager
                .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)
    }
}
