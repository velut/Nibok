package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.activity.ChatActivity
import com.nibokapp.nibok.ui.adapter.ViewTypeAdapter
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.MessagePresenter
import kotlinx.android.synthetic.main.fragment_messages.*
import org.jetbrains.anko.startActivity

/**
 * Fragment managing the messaging between users.
 */
class MessagesFragment : ViewTypeFragment() {

    companion object {
        private val TAG = MessagesFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_messages

    override fun getFragmentName() : String = TAG

    // Presenter

    override fun getFragmentPresenter() = MessagePresenter()

    // Main View

    override fun getMainView() : RecyclerView = messagesList

    override fun getMainViewId() = R.id.messagesList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter(messageItemClickListener)

    // Main View Data

    override fun onMainViewScrollDownLoader() = {}()

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListMessages

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = ViewTypeAdapter(messageItemClickListener)

    override fun getSearchHint() : String = getString(R.string.search_hint_message)


    override fun handleRefreshAction() {
        // TODO Implement
        Log.d(TAG, "TODO")
    }

    private val messageItemClickListener = object : ViewTypeAdapter.ItemClickListener {
        override fun onButtonClick(itemId: Long, itemType: Int) {
        }

        override fun onItemClick(itemId: Long, itemType: Int) {
            // TODO Implement
            Log.d(TAG, "Clicked $itemId")
            context.startActivity<ChatActivity>()
        }
    }
}