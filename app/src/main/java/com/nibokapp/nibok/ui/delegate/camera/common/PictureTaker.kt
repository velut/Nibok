package com.nibokapp.nibok.ui.delegate.camera.common

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Interface for classes delegated to taking pictures.
 */
interface PictureTaker {

    /**
     * Perform the steps necessary to take a picture
     * and then return the arguments needed for calling startActivityForResult().
     *
     * @param context a [Context]
     *
     * @return a [Pair]<[Intent], [Int]> used to start the picture taking activity
     *
     * @throws IllegalStateException if any condition necessary to launch the activity is not satisfied
     */
    fun getStartPictureTakingActivityArgs(context: Context): Pair<Intent, Int>

    /**
     * Elaborate the result returned from the picture taking activity.
     * To be called in onActivityResult().
     *
     * @param context a [Context]
     * @param requestCode the request code provided by onActivityResult()
     * @param resultCode the result code provided by onActivityResult()
     * @param data the data provided by onActivityResult()
     *
     * @return the [Uri] of the picture if picture taking was successful, null otherwise
     */
    fun onPictureTakingActivityResult(context: Context,
                                      requestCode: Int, resultCode: Int, data: Intent?): Uri?

    /**
     * Set the current picture [Uri].
     *
     * @param uri the current picture [Uri]
     */
    fun setCurrentPictureUri(uri: Uri)

    /**
     * Get the current picture [Uri].
     *
     * @return the current picture [Uri]
     */
    fun getCurrentPictureUri(): Uri
}
