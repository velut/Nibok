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

    private var errorDialog: MaterialDialog? = null


    override fun getFragmentLayout(): Int = R.layout.fragment_publish_input_insertion_picture

    override fun getInputContainer(): View = inputInsertionPicturesContainer

    override fun getDialogs(): List<MaterialDialog?> = listOf(errorDialog)

    override fun hasValidData(): Boolean = true

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
        updateTakePictureButton()
        displayPictures()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult")
        val picture = onPictureTakingActivityResult(context, requestCode, resultCode, data)
        Log.d(TAG, "Result picture: $picture")
        picture?.let {
            pictures.add(it)
            updateTakePictureButton()
            displayPictures()
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
     * Update the picture taking button.
     * If the user has taken the maximum number of pictures then disable the button.
     *
     * @return true if the button is enabled, false otherwise
     */
    private fun updateTakePictureButton(): Boolean {
        if (pictures.size == MAX_PICTURES) {
            disableTakePictureButton()
            return false
        }
        return true
    }

    private fun disableTakePictureButton() {
        btnTakePicture.apply {
            isEnabled = false
            visibility = View.GONE
        }
    }

    private fun displayPictures() {
        loadPictures()
        scrollToLastPicture()
    }

    private fun loadPictures() {
        pictures.take(MAX_PICTURES).forEachIndexed { i, uri ->
            val host = pictureHosts[i]
            host.apply {
                visibility = View.VISIBLE
                loadImg(uri.toString())
            }
        }
    }

    private fun scrollToLastPicture() {
        pictureScrollView.postDelayed(
                { pictureScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) },
                120L
        )
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
}

