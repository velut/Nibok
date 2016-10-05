package com.nibokapp.nibok.extension

import android.content.Context
import com.nibokapp.nibok.ui.activity.AuthenticateActivity
import com.nibokapp.nibok.ui.activity.ChatActivity
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.fragment.ChatFragment
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
fun Context.startDetailActivity(insertionId: String) =
        this.startActivity<InsertionDetailActivity>(
                InsertionDetailFragment.INSERTION_ID to insertionId)

/**
 * Start the AuthenticateActivity.
 */
fun Context.startAuthenticateActivity() =
        this.startActivity<AuthenticateActivity>()

/**
 * Start the ConversationActivity for the given conversation's id.
 */
fun Context.startConversation(conversationId: String) =
        this.startActivity<ChatActivity>(
                ChatFragment.CONVERSATION_ID to conversationId
        )