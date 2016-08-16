package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import kotlinx.android.synthetic.main.fragment_selling.*

/**
 * Fragment managing the books being sold the user.
 */
class SellingFragment : ViewTypeFragment() {

    companion object {
        private val TAG = SellingFragment::class.java.simpleName
    }

    override fun getFragmentLayout() = R.layout.fragment_selling

    override fun getMainView(): RecyclerView = sellingBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = BookAdapter()

    override fun getSearchView(): RecyclerView = getMainView() // TODO add search view and methods

    override fun getSearchViewLayoutManager() = getMainViewLayoutManager()

    override fun getMainViewData(): List<ViewType> = emptyList()

    override fun getSearchViewAdapter() = getMainViewAdapter()

    override fun searchStrategy(query: String): List<ViewType> = emptyList() // TODO add real search

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    override fun onMainViewScrollDownLoader() {
        Log.i(TAG, "Loading more books being sold")
    }

    override fun getFragmentName() : String = TAG

    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }
}