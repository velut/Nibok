package com.nibokapp.nibok.ui.presenter.detail

import android.util.Log
import com.nibokapp.nibok.authentication.Authenticator
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.domain.command.bookinsertion.RequestBookInsertionByIdCommand
import com.nibokapp.nibok.domain.command.conversation.StartConversationCommand
import com.nibokapp.nibok.domain.command.user.RequestLocalUserIdCommand
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Presenter that retrieves full details about an insertion.
 */
class InsertionDetailPresenter(
        val authenticator: AuthenticatorInterface = Authenticator
) {

    companion object {
        private val TAG = InsertionDetailPresenter::class.java.simpleName
    }

    /**
     * Get the id of the local user if it exists.
     *
     * @return the id of the local user if it exists, null otherwise
     */
    fun getUserId(): String? {
        Log.d(TAG, "Requesting local user id")
        return try {
            RequestLocalUserIdCommand().execute()
        } catch (e: IllegalStateException) {
            null
        }
    }

    /**
     * Check if there is a currently logged in user or not.
     *
     * @return true if there is a currently logged in user, false otherwise
     */
    fun loggedUserExists(): Boolean {
        return authenticator.loggedUserExists()
    }

    /**
     * Get detailed data about the insertion with the given id.
     *
     * @param insertionId the id of the insertion
     *
     * @return detail data about the insertion if the insertion was found, null otherwise
     */
    fun getInsertionDetails(insertionId: String): BookInsertionModel? {
        Log.d(TAG, "Getting book insertion $insertionId details")
        return RequestBookInsertionByIdCommand(insertionId).execute()
    }

    /**
     * Start a conversation with the partner with the given id.
     *
     * @param partnerId the id of the partner
     *
     * @return the id of the started conversation
     * or null if the conversation could not be started
     */
    fun startConversation(partnerId: String): String? {
        Log.d(TAG, "Starting conversation with: $partnerId")
        return StartConversationCommand(partnerId).execute()
    }
}