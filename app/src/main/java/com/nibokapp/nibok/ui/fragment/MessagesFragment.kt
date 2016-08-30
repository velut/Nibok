package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import kotlinx.android.synthetic.main.fragment_messages.*

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

    // Main View

    override fun getMainView() : RecyclerView = messagesList

    override fun getMainViewId() = R.id.messagesList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter()

    // Main View Data

    override fun getMainViewData(): List<ViewType> = emptyList()

    override fun onMainViewScrollDownLoader() = {}()

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListMessages

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = ViewTypeAdapter()

    // Search View Data

    override fun searchStrategy(query: String): List<ViewType> = BookManager.getBooksFromQuery(query)

    override fun handleRefreshAction() {
        // TODO Implement
        Log.d(TAG, "TODO")
    }
}