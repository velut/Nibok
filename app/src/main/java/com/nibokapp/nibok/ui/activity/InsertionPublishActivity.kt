package com.nibokapp.nibok.ui.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData
import com.nibokapp.nibok.extension.onPageSelected
import com.nibokapp.nibok.ui.App
import com.nibokapp.nibok.ui.fragment.publish.*
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.activity_insertion_publish.*


/**
 * InsertionPublishActivity hosts the insertion publishing process.
 * It also implements the PublishProcessManager interface to enable screen navigation
 * between fragments of the publishing process and also to keep track of submitted data.
 */
class InsertionPublishActivity : AppCompatActivity(), BasePublishFragment.PublishProcessManager {

    companion object {
        private val TAG = InsertionPublishActivity::class.java.simpleName

        /**
         * Permissions.
         */
        private val PERMISSION_WRITE_EXTERNAL_STORAGE = App.PERMISSION_WRITE_EXTERNAL_STORAGE
        private val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = App.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE

        private val KEY_INSERTION_DATA = "$TAG:insertionData"


        /**
         * The number of fragments used for the publishing process.
         */
        val NUM_PUBLISH_FRAGMENTS = 5

        /**
         * Get a publishing fragment instance based on the position in the publishing process.
         */
        val PUBLISH_FRAGMENT = fun (position: Int): Fragment = when (position) {
            0 -> InputIsbn()
            1 -> InputBookData()
            2 -> InputInsertionData()
            3 -> InputInsertionPicture()
            4 -> FinalizeInsertion()
            else -> throw IndexOutOfBoundsException("No publishing fragment available for this position")
        }
    }

    private var alertQuitDialog: MaterialDialog? = null

    private var insertionData: InsertionData = InsertionData()


    override fun prevScreen() = with(publishViewPager) {
        Log.d(TAG, "Prev;\n$insertionData")
        currentItem -= 1
    }

    override fun nextScreen() = with(publishViewPager) {
        Log.d(TAG, "Next;\n$insertionData")
        currentItem += 1
    }

    override fun getInsertionData(): InsertionData = insertionData.copy()

    override fun resetData() {
        insertionData = InsertionData()
    }

    override fun resetBookData() {
        insertionData.bookData = BookData()
    }

    override fun setIsbn(isbn: String) {
        insertionData.bookData.isbn = isbn
    }

    override fun isIsbnSet(): Boolean = insertionData.bookData.isbn != ""

    override fun setBookData(data: BookData) = with(insertionData) {
        val isbn = bookData.isbn

        if (bookData.id == "" || bookData.differsFrom(data)) {
            bookData = data
            bookData.isbn = isbn
        }
    }

    override fun setPrice(price: Float) {
        insertionData.bookPrice = price
    }

    override fun setWearCondition(conditionId: Int) {
        insertionData.bookConditionId = conditionId
    }

    override fun setPictures(pictures: List<String>) {
        insertionData.bookPictures = pictures
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion_publish)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        savedInstanceState?.let {
            insertionData = it.getParcelable(KEY_INSERTION_DATA) ?: InsertionData()
        }
        Log.d(TAG, "Saved insertion data:\n$insertionData")

        setupViewPager()
    }

    override fun onStart() {
        super.onStart()
        if (!hasWriteExternalStoragePermission()) {
            requestWriteExternalStoragePermission()
        } else {
            Log.d(TAG, "Write external storage permission is already granted")
        }
    }

    private fun hasWriteExternalStoragePermission(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(this, PERMISSION_WRITE_EXTERNAL_STORAGE)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(PERMISSION_WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Write external storage permission granted")
            } else {
                Log.d(TAG, "Write external storage permission NOT granted, exit")
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_INSERTION_DATA, insertionData)
    }

    override fun onPause() {
        super.onPause()
        dismissDialogs()
    }

    override fun onBackPressed() {
        alertBeforeQuit { super.onBackPressed() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        @SuppressLint("PrivateResource")
        when (item.itemId) {
            android.R.id.home -> { // Handle toolbar's UP button
                alertBeforeQuit { NavUtils.navigateUpFromSameTask(this) }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Alert the user with a dialog before quitting the insertion publishing activity.
     * If the user decides to quit call the onQuit function.
     *
     * @param onQuit the function to be called if the user decides to quit the activity
     */
    private fun alertBeforeQuit(onQuit: () -> Unit) {
        alertQuitDialog = MaterialDialog.Builder(this)
                .title(R.string.alert_quit_publish_title)
                .content(R.string.alert_quit_publish_content)
                .positiveText(R.string.alert_quit_publish_positive_text)
                .negativeText(R.string.text_cancel)
                .onPositive { materialDialog, dialogAction ->  onQuit() }
                .build()
        alertQuitDialog?.show()
    }

    /**
     * Dismiss eventual open dialogs to prevent leaked windows.
     */
    private fun dismissDialogs() {
        Log.d(TAG, "Dismissing dialogs")
        val dialogs = listOf(alertQuitDialog)
        dialogs.forEach { it?.dismiss() }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        publishViewPager.adapter = adapter
        publishViewPager.onPageSelected { position ->
            Log.d(TAG, "Selected Publish Page: $position")
            val selectedFragment = adapter.getItem(position) as? BasePublishFragment
            selectedFragment?.onBecomeVisible()
        }
    }


    class ViewPagerAdapter(val fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            Log.d(TAG, "Get item called for position: $position")
            val fragmentTag = getFragmentTagForPosition(position)
            val foundFragment = fm.findFragmentByTag(fragmentTag)

            if (foundFragment != null) {
                Log.d(TAG, "Found publish fragment: $foundFragment for tag: $fragmentTag")
                return foundFragment
            }

            val newFragment = PUBLISH_FRAGMENT(position)
            Log.d(TAG, "No fragment found, returning a new one: $newFragment")
            return newFragment
        }

        override fun getCount(): Int = NUM_PUBLISH_FRAGMENTS

        /**
         * Return the tag given by the adapter to the fragment.
         *
         * @return the string representing the tag of the fragment at the given position
         */
        private fun getFragmentTagForPosition(position: Int) =
                "android:switcher:${R.id.publishViewPager}:$position"
    }
}