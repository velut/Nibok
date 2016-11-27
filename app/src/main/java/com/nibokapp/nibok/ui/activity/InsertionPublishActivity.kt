package com.nibokapp.nibok.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData
import com.nibokapp.nibok.extension.onPageSelected
import com.nibokapp.nibok.ui.fragment.publish.*
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.activity_insertion_publish.*


/**
 * InsertionPublishActivity hosts the insertion publishing process.
 * It also implements the PublishProcessManager interface to enable screen navigation
 * between fragments of the publishing process and also to keep track of submitted data.
 */
class InsertionPublishActivity() : AppCompatActivity(), BasePublishFragment.PublishProcessManager {

    companion object {
        private val TAG = InsertionPublishActivity::class.java.simpleName

        private val KEY_INSERTION_DATA = "$TAG:insertionData"
    }

    private val fragments: List<BasePublishFragment> by lazy {
        listOf(
                InputIsbn(),
                InputBookData(),
                InputInsertionData(),
                InputInsertionPicture(),
                FinalizeInsertion()
        )
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
        Log.d(TAG, "InsData isbn: ${insertionData.bookData.isbn}")

        setupViewPager()
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
     * Check if data about the book contained in this BookData differs from data passed in the other object.
     *
     * @param other the other BookData object
     *
     * @return true if book data differs, false otherwise
     */
    private fun BookData.differsFrom(other: BookData): Boolean =
            this.title.toLowerCase() != other.title.toLowerCase() ||
                    this.authors.map(String::toLowerCase).toSet() != other.authors.map(String::toLowerCase).toSet() ||
                    this.year != other.year ||
                    this.publisher.toLowerCase() != other.publisher.toLowerCase()

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
                .negativeText(getString(R.string.text_cancel))
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
        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        publishViewPager.adapter = adapter
        publishViewPager.onPageSelected {
            val selectedFragment = fragments.getOrNull(it)
            selectedFragment?.onBecomeVisible()
        }
    }

    class ViewPagerAdapter(val fm: FragmentManager,
                            val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // If the fragment manager already has a fragment with the given tag
            // then return the stored fragment instead of a new instance
            // This solves the problems of duplicate fragments and blank views after rotation
            // caused by the viewpager
            val foundFragment = fm.findFragmentByTag(getFragmentTagForPosition(position))
            return foundFragment ?: fragments[position]
        }

        override fun getCount(): Int = fragments.size

        /**
         * Return the tag given by the adapter to the fragment.
         *
         * @return the string representing the tag of the fragment at the given position
         */
        fun getFragmentTagForPosition(position: Int) =
                "android:switcher:${R.id.publishViewPager}:$position"
    }
}