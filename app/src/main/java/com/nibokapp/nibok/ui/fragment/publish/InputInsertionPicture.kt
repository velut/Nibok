package com.nibokapp.nibok.ui.fragment.publish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.ui.delegate.camera.PictureTakerImpl
import com.nibokapp.nibok.ui.delegate.camera.common.PictureTaker
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.fragment_publish_input_insertion_picture.*
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
     * List of [ImageView] that hold the pictures taken by the user.
     */
    private val pictureHosts: List<ImageView> by lazy {
        listOf(picView1, picView2, picView3, picView4, picView5)
    }

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
        getPublishManager().setPictures(pictures.map(Uri::toString))
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
        resetHosts()
        pictures.take(MAX_PICTURES).forEachIndexed { i, uri ->
            val host = pictureHosts[i]
            host.apply {
                visibility = View.VISIBLE
                loadImg(uri.toString())
                addListeners(i)
            }
        }
    }

    private fun resetHosts() {
        pictureHosts.forEach {
            it.apply {
                visibility = View.GONE
                loadImg("")
            }
        }
    }

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
        try {
            pictureScrollView.postDelayed(
                    { pictureScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) },
                    120L
            )
        } catch (e: NullPointerException) {
            Log.e(TAG, e.toString())
        }
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
