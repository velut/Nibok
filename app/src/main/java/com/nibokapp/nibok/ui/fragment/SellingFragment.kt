package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.fragment.common.BookFragment
import kotlinx.android.synthetic.main.fragment_selling.*

/**
 * Fragment managing the books being sold the user.
 */
class SellingFragment : BookFragment() {

    companion object {
        private val TAG = SellingFragment::class.java.simpleName
    }

    override fun getFragmentLayout() = R.layout.fragment_selling

    override fun getBooksViewLayoutManager() = LinearLayoutManager(context)

    override fun getBooksView(): RecyclerView = sellingBooksList

    override fun getBooksViewAdapter() = BookAdapter()

    override fun onScrollDownLoader() {
        Log.i(TAG, "Loading more books being sold")
    }

    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }
}