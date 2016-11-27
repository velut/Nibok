package com.nibokapp.nibok.ui.fragment.publish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.ui.delegate.camera.PictureTakerImpl
import com.nibokapp.nibok.ui.delegate.camera.common.PictureTaker
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.fragment_publish_input_insertion_picture.*
import org.jetbrains.anko.findOptional
import java.util.*

/**
 * Publishing fragment managing the pictures that the user can take.
 *
 * @param pictureTaker the [PictureTaker] implementation that manages camera operations.
 * By default this duty is delegated to [PictureTakerImpl]
 */
class InputInsertionPicture(
        private val pictureTaker: PictureTaker = PictureTakerImpl()
) : BasePublishFragment(), PictureTaker by pictureTaker {

    companion object {
        private val TAG = InputInsertionPicture::class.java.simpleName

        private val KEY_CURRENT_PICTURE = "$TAG:currentPicture"
        private val KEY_LIST_PICTURE = "$TAG:listPicture"

        private const val MAX_PICTURES = 5
    }


    /**
     * List of [ImageView] holding the pictures taken by the user.
     */
    private val pictureHosts: MutableList<ImageView> = mutableListOf()

    /**
     * [MutableList] of [Uri] of taken pictures.
     */
    private val pictures: MutableList<Uri> = mutableListOf()

    private val canTakePictures: Boolean
        get() = pictures.size < MAX_PICTURES

    private var errorDialog: MaterialDialog? = null
    private var deleteDialog: MaterialDialog? = null


    override fun getFragmentLayout(): Int = R.layout.fragment_publish_input_insertion_picture

    override fun getInputContainer(): View = inputInsertionPicturesContainer

    override fun getDialogs(): List<MaterialDialog?> = listOf(errorDialog, deleteDialog)

    override fun hasValidData(): Boolean = true

    override fun saveData() {
        publishProcessManager.setPictures(pictures.map(Uri::toString))
    }

    override fun setupInput() {
        setupPictureTakingInput()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            val pics = it.getParcelableArrayList<Uri>(KEY_LIST_PICTURE)
            pictures.addAll(pics)

            val uri = it.getParcelable<Uri>(KEY_CURRENT_PICTURE)
            uri?.let { setCurrentPictureUri(it) }
        }
        updatePicView()
        scrollToLastPicture()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")
        val picture = onPictureTakingActivityResult(context, requestCode, resultCode, data)
        Log.d(TAG, "Result picture: $picture")
        picture?.let {
            pictures.add(it)
            updatePicView()
            scrollToLastPicture()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelable(KEY_CURRENT_PICTURE, getCurrentPictureUri())
            putParcelableArrayList(KEY_LIST_PICTURE, pictures.toCollection(ArrayList<Uri>()))
        }
    }

    private fun setupPictureTakingInput() {
        btnTakePicture.setOnClickListener {
            try {
                val (intent, reqCode) = getStartPictureTakingActivityArgs(context)
                startActivityForResult(intent, reqCode)
            } catch (e: IllegalStateException) {
                Log.e(TAG, e.toString())
                showErrorDialog()
            }
        }
    }

    /**
     * Update the view that hosts the pictures and the button used to take pictures.
     */
    private fun updatePicView() {
        Log.d(TAG, "Updating pictures view")
        updateTakePictureButton()
        bindPicturesToHosts()
    }

    /**
     * Update the picture taking button.
     * If the user has taken the maximum number of pictures then disable and hide the button.
     * If the user can still take pictures then enable and show the button.
     */
    private fun updateTakePictureButton() {
        if (!canTakePictures) {
            disableTakePictureButton()
        } else {
            enableTakePictureButton()
        }
    }

    private fun disableTakePictureButton() {
        btnTakePicture.apply {
            isEnabled = false
            visibility = View.GONE
        }
    }

    private fun enableTakePictureButton() {
        btnTakePicture.apply {
            isEnabled = true
            visibility = View.VISIBLE
        }
    }

    /**
     * Reset the hosts and then bind MAX_PICTURES from the current list of pictures
     * to the ImageView hosts.
     */
    private fun bindPicturesToHosts() {
        Log.d(TAG, "Binding pictures to hosts")
        resetHosts()
        Log.d(TAG, "Hosts reset, binding pictures")
        pictures.take(MAX_PICTURES).forEachIndexed { i, uri ->
            // All hosts have a margin at the end except the last one
            val hasEndMargin = i != MAX_PICTURES -1
            val host = getPictureHost(hasEndMargin)
            host.apply {
                loadImg(uri.toString())
                addListeners(i)
            }
            // Insert as last picture before the picture taking button
            val secondToLastPos = Math.max(0, picturesContainer.childCount - 2)
            picturesContainer.addView(host, secondToLastPos)
            pictureHosts.add(host)
            Log.d(TAG, "Host $i bound")
        }
    }

    /**
     * Remove old hosts from the LinearLayout hosting the pictures.
     */
    private fun resetHosts() {
        pictureHosts.forEach {
            picturesContainer.removeView(it)
        }
    }

    /**
     * Get an [ImageView] host to display a picture taken with the camera.
     *
     * @param hasEndMargin true if this ImageView must have an end margin, false otherwise
     *
     * @return an [ImageView]
     */
    private fun getPictureHost(hasEndMargin: Boolean): ImageView {
        // LayoutParams
        val dp170 = getPixelsForDp(170f)
        val dp24 = getPixelsForDp(24f)
        // Square dimensions for ImageView
        val lParams = LinearLayout.LayoutParams(dp170, dp170)
        if (hasEndMargin) lParams.marginEnd = dp24

        // ImageView
        return ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = lParams
            contentDescription = getString(R.string.picture_button_description)
        }
    }

    /**
     * Get the value in pixels for a given value in DP.
     *
     * @param dpValue the float value in DP
     *
     * @return the number of pixels corresponding to the given value in DP
     */
    private fun getPixelsForDp(dpValue: Float): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.displayMetrics).toInt()

    /**
     * Add two listeners to this ImageView.
     * On click open the gallery centered at the picture hosted by this ImageView,
     * on click and hold show a dialog asking to delete the picture.
     */
    private fun ImageView.addListeners(picPos: Int) {
        // Open pic gallery on click
        setOnClickListener {
            ImageViewer.Builder(context, pictures.map(Uri::toString).toTypedArray())
                    .setStartPosition(picPos)
                    .show()
        }

        // Offer option to delete picture on holding down
        setOnLongClickListener {
            showDeleteDialog(picPos)
            true // Long click has been handled
        }
    }

    private fun scrollToLastPicture() {
        Handler().postDelayed({
            val picScrollView = view?.findOptional<HorizontalScrollView>(R.id.pictureScrollView)
            picScrollView?.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }, 500L)
    }

    private fun showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = getErrorDialog()
        }
        errorDialog?.show()
    }

    private fun getErrorDialog(): MaterialDialog {
        return MaterialDialog.Builder(context)
                .title(R.string.error_generic)
                .content(R.string.error_take_picture)
                .positiveText(android.R.string.ok)
                .build()
    }

    private fun showDeleteDialog(picPos: Int) {
        deleteDialog = MaterialDialog.Builder(context)
                .title(R.string.dialog_delete_pic_title)
                .content(R.string.dialog_delete_pic_content)
                .negativeText(R.string.text_cancel)
                .positiveText(R.string.dialog_delete_pic_positive)
                .onPositive { materialDialog, dialogAction -> deletePicAtPos(picPos) }
                .build()
        deleteDialog?.show()
    }

    /**
     * Delete the picture at the given position in the pictures list.
     * Update the picture view after deletion.
     *
     * @param pos the position of the picture to delete
     */
    private fun deletePicAtPos(pos: Int) {
        val picUri = pictures.removeAt(pos)
        context.contentResolver.delete(picUri, null, null)
        updatePicView()
    }
}
