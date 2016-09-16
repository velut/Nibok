package com.nibokapp.nibok.ui.presenter.viewtype

import android.util.Log
import com.nibokapp.nibok.domain.command.conversation.*
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter

/**
 * Presenter that operates on messages conversations.
 */
class MessagePresenter : ViewTypePresenter {

    companion object {
        private val TAG = MessagePresenter::class.java.simpleName
    }

    override fun getData(): List<ViewType> {
        Log.d(TAG, "Getting conversation data")
        return RequestUserConversationListCommand().execute()
    }

    override fun getCachedData(): List<ViewType> {
        Log.d(TAG, "Getting cached conversation data")
        return RequestCachedUserConversationListCommand().execute()
    }

    override fun getDataNewerThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting newer conversation data")
        return RequestNewerConversationCommand(item as ConversationModel).execute()
    }

    override fun getDataOlderThanItem(item: ViewType): List<ViewType> {
        Log.d(TAG, "Getting older conversation data")
        return RequestOlderConversationCommand(item as ConversationModel).execute()
    }

    override fun getQueryData(query: String): List<ViewType> {
        Log.d(TAG, "Getting query conversation data")
        return RequestConversationListFromQueryCommand(query).execute()
    }
}
