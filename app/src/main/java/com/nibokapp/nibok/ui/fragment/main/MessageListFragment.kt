package com.nibokapp.nibok.ui.fragment.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.activity.ChatActivity
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.fragment.ChatFragment
import com.nibokapp.nibok.ui.fragment.main.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.MessagePresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import kotlinx.android.synthetic.main.fragment_message_list.*
import org.jetbrains.anko.startActivity

/**
 * Fragment managing the messaging between users.
 */
class MessageListFragment(
        val presenter: ViewTypePresenter = MessagePresenter()
) : ViewTypeFragment() {

    companion object {
        private val TAG = MessageListFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_message_list

    override fun getFragmentName() : String = TAG

    // Presenter

    override fun getFragmentPresenter() = presenter

    // Main View

    override fun getMainView() : RecyclerView = messagesList

    override fun getMainViewId() = R.id.messagesList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter(messageItemClickListener)

    // Main View Data

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListMessages

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = ViewTypeAdapter(messageItemClickListener)

    override fun getSearchHint() : String = getString(R.string.search_hint_message)

    // Refresh

    override fun getNoNewerItemsFromRefreshString(): String = getString(R.string.no_newer_messages)

    private val messageItemClickListener = object : ViewTypeAdapter.ItemClickListener {
        override fun onButtonClick(itemId: Long, itemType: Int) {
        }

        override fun updateItemOnButtonClick(): Boolean = false

        override fun onItemClick(itemId: Long, itemType: Int) {
            Log.d(TAG, "Opening chat for conversation: $itemId")
            context.startActivity<ChatActivity>(
                    ChatFragment.CONVERSATION_ID to itemId
            )
        }
    }
}