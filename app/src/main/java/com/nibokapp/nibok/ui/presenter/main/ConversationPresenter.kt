package com.nibokapp.nibok.ui.presenter.main

import android.util.Log
import com.nibokapp.nibok.domain.command.conversation.RequestCachedUserConversationListCommand
import com.nibokapp.nibok.domain.command.conversation.RequestConversationListFromQueryCommand
import com.nibokapp.nibok.domain.command.conversation.RequestOlderConversationCommand
import com.nibokapp.nibok.domain.command.conversation.RequestUserConversationListCommand
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Presenter used for the conversations in which the user is engaged.
 */
class ConversationPresenter : MainActivityPresenter<ConversationModel> {

    companion object {
        private val TAG = ConversationModel::class.java.simpleName
    }

    override fun getData(): List<ConversationModel> {
        Log.d(TAG, "Requesting conversation data")
        return RequestUserConversationListCommand().execute()
    }

    override fun getCachedData(): List<ConversationModel> {
        Log.d(TAG, "Requesting cached conversation data")
        return RequestCachedUserConversationListCommand().execute()
    }

    override fun getDataOlderThanItem(item: ConversationModel): List<ConversationModel> {
        Log.d(TAG, "Requesting older conversation data")
        return RequestOlderConversationCommand(item).execute()
    }

    override fun getQueryData(query: String): List<ConversationModel> {
        Log.d(TAG, "Requesting conversation data by query")
        return RequestConversationListFromQueryCommand(query).execute()
    }
}
