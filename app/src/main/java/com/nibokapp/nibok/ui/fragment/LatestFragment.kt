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

/**
 * Fragment managing the feed of books.
 *
 * It fetches the latest books published on the platform, it requests newer and older books.
 */
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

        // Setup the list of the latest books
        latestBooksList.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            clearOnScrollListeners()
            // Add infinite scroll listener
            addOnScrollListener(InfiniteScrollListener(linearLayoutManager) {
                // Request older books published on the platform on load
                requestOlderBooks()
            })
        }

        initAdapter()

        val latestBooks = BookManager.getBooksList()
        (latestBooksList.adapter as BookAdapter).addBooks(latestBooks)
    }

    override fun handleRefreshAction() {
        super.handleRefreshAction()
        // If new books are available add them to the list and return to the top
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

    /**
     * Function passed to the infinite scroll listener to load older books.
     *
     * It requests books published on the platform before the ones currently displayed.
     *
     * If older books are available they are added to the bottom of the list,
     * otherwise a message is shown and the loading item is removed.
     */
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

    /**
     * Associates BookAdapter to the latest book list.
     */
    private fun initAdapter() {
        if (latestBooksList.adapter == null) {
            latestBooksList.adapter = BookAdapter()
        }
    }
}