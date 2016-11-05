package com.nibokapp.nibok.ui.delegate.camera.common

import android.app.Activity
import android.content.Intent
import android.net.Uri

/**
 * Interface for classes delegated to taking pictures.
 */
interface PictureTaker {

    /**
     * Set the Activity to be used as the context in order to dispatch picture taking intents.
     *
     * @param activity the activity context
     */
    fun setActivityContext(activity: Activity)

    /**
     * Clear the previously set activity context.
     */
    fun clearActivityContext()

    /**
     * Start the camera activity that will take pictures.
     *
     * @throws IllegalStateException if any condition necessary to launch the activity is not satisfied
     */
    fun startPictureTakingActivity()

    /**
     * Elaborate the result returned from the picture taking activity.
     * To be called in onActivityResult().
     *
     * @param requestCode the request code provided by onActivityResult()
     * @param resultCode the result code provided by onActivityResult()
     * @param data the data provided by onActivityResult()
     *
     * @return the Uri of the picture if picture taking was successful, null otherwise
     */
    fun onPictureTakingActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Uri?

    /**
     * Set the current picture Uri.
     *
     * @param uri the current picture Uri
     */
    fun setCurrentPictureUri(uri: Uri)

    /**
     * Get the current picture Uri.
     *
     * @return the current picture Uri
     */
    fun getCurrentPictureUri() : Uri
}
