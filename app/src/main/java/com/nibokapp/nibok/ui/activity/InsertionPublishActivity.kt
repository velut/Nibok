package com.nibokapp.nibok.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.publish.*
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.activity_insertion_publish.*

/**
 * InsertionPublishActivity manages insertion publishing.
 */
class InsertionPublishActivity : AppCompatActivity(), BasePublishFragment.PublishScreenManager {

    companion object {
        private val TAG = InsertionPublishActivity::class.java.simpleName
    }

    private val fragments: List<Fragment> by lazy {
        listOf(
                InputIsbn(),
                InputBookData(),
                InputInsertionData(),
                InputInsertionPicture(),
                FinalizeInsertion()
        )
    }

    private var alertQuitDialog: MaterialDialog? = null

    private val dialogs: List<MaterialDialog?> = listOf(alertQuitDialog)


    override fun prevScreen() = with(publishViewPager) {
        currentItem -= 1
    }

    override fun nextScreen() = with(publishViewPager) {
        currentItem += 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion_publish)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewPager()
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
                .negativeText(getString(R.string.text_cancel))
                .onPositive { materialDialog, dialogAction ->  onQuit() }
                .build()
        alertQuitDialog?.show()
    }

    /**
     * Dismiss eventual open dialogs to prevent leaked windows.
     */
    private fun dismissDialogs() {
        dialogs.forEach { it?.dismiss() }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        publishViewPager.adapter = adapter
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