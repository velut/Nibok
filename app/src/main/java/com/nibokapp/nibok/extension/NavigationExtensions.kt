package com.nibokapp.nibok.extension

import android.content.Context
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.activity.LoginActivity
import com.nibokapp.nibok.ui.fragment.InsertionDetailFragment
import org.jetbrains.anko.startActivity

/**
 * Extension file with common app navigation methods.
 */

/**
 * Start the InsertionDetailActivity about the insertion with the given id.
 *
 * @param insertionId the id of the insertion to display in the InsertionDetailActivity
 */
fun Context.startDetailActivity(insertionId: Long) =
        this.startActivity<InsertionDetailActivity>(
                InsertionDetailFragment.INSERTION_ID to insertionId)

/**
 * Start the LoginActivity.
 */
fun Context.startLoginActivity() =
        this.startActivity<LoginActivity>()