package com.nibokapp.nibok.ui.fragment.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.extension.startChatActivity
import com.nibokapp.nibok.ui.adapter.main.ConversationAdapter
import com.nibokapp.nibok.ui.adapter.main.UpdatableAdapter
import com.nibokapp.nibok.ui.fragment.main.common.MainUpdatableAdapterFragment
import com.nibokapp.nibok.ui.presenter.main.ConversationPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter


/**
 * Fragment used to display the conversations in which the user is engaged.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class ConversationFragment(
        override val presenter: MainActivityPresenter<ConversationModel> = ConversationPresenter()
) : MainUpdatableAdapterFragment<ConversationModel>() {

    companion object {
        private val TAG = PublishedFragment::class.java.simpleName
    }

    override val TAG: String = Companion.TAG

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_conversation

    /*
     * Main view
     */

    override val mainViewId: Int = R.id.conversationList

    override val mainAdapter: ConversationAdapter = ConversationAdapter { startChatActivity(it) }

    override val mainLayoutManager: LinearLayoutManager
        get() = LinearLayoutManager(context)

    override val mainScrollListener: RecyclerView.OnScrollListener?
        get() = buildMainViewInfiniteScrollListener()

    /*
     * Search view
     */

    override val searchViewId: Int = R.id.searchConversationList

    override val searchAdapter: ConversationAdapter = ConversationAdapter { startChatActivity(it) }

    override val searchLayoutManger: LinearLayoutManager
        get() = LinearLayoutManager(context)

    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_message) }

    override val noSearchResultsHintId: Int = R.string.search_hint_no_conversations

    /*
     * Updatable adapters
     */

    override val mainUpdatableAdapter: UpdatableAdapter<ConversationModel>? = mainAdapter

    override val searchUpdatableAdapter: UpdatableAdapter<ConversationModel>? = searchAdapter


    private fun startChatActivity(conversationId: String) {
        context.startChatActivity(conversationId)
    }
}