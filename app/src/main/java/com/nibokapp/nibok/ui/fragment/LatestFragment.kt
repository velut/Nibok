package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.adapter.common.InfiniteScrollListener
import com.nibokapp.nibok.ui.fragment.common.BaseFragment
import kotlinx.android.synthetic.main.latest_fragment.*
import org.jetbrains.anko.toast

class LatestFragment : BaseFragment() {

    companion object {
        private val TAG = LatestFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.latest_fragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context)

        latestBooksList.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            clearOnScrollListeners()
            addOnScrollListener(InfiniteScrollListener(linearLayoutManager) {
                requestOlderBooks()
            })
        }

        initAdapter()

        val latestBooks = BookManager.getBooksList()
        (latestBooksList.adapter as BookAdapter).addBooks(latestBooks)
    }

    override fun handleRefreshAction() {
        super.handleRefreshAction()
        if (BookManager.hasNewerBooks()) {
            val newerBooks = BookManager.getNewerBooks()
            (latestBooksList.adapter as BookAdapter).addBooks(newerBooks)
            handleBackToTopAction()
        } else {
            context.toast(getString(R.string.no_newer_books))
        }
    }

    override fun handleBackToTopAction() {
        latestBooksList.layoutManager.scrollToPosition(0)
    }

    private fun requestOlderBooks() {
        val bookAdapter = latestBooksList.adapter as BookAdapter

        if (BookManager.hasOlderBooks()) {
            Log.i(TAG, "Requesting older books on scroll down")
            val olderBooks = BookManager.getOlderBooks()
            bookAdapter.addBooks(olderBooks, addToTop = false)
        } else {
            Log.i(TAG, "No more older books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }

    private fun initAdapter() {
        if (latestBooksList.adapter == null) {
            latestBooksList.adapter = BookAdapter()
        }
    }
}