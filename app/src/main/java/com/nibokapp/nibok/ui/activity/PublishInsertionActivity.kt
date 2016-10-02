package com.nibokapp.nibok.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.PublishInsertionFragment

/**
 * PublishInsertionActivity.
 * This activity hosts the PublishInsertionFragment
 * and alerts the user with a dialog before quitting this activity.
 */
class PublishInsertionActivity : AppCompatActivity() {

    private var alertQuitDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_insertion)

        // If restoring do not create overlapping fragments
        if (savedInstanceState != null) {
            return
        }

        val publishFragment = PublishInsertionFragment()

        // Pass eventual intent extras to the fragment
        publishFragment.arguments = intent.extras

        // Add the fragment to the container
        supportFragmentManager.beginTransaction()
                .add(R.id.publishFragmentContainer, publishFragment)
                .commit()
    }

    override fun onPause() {
        alertQuitDialog?.dismiss()
        super.onPause()
    }

    override fun onBackPressed() {
        alertBeforeQuit {
            super.onBackPressed()
        }
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
}
