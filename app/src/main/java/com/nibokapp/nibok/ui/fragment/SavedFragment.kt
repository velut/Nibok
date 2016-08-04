package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.fragment.common.BookFragment
import kotlinx.android.synthetic.main.fragment_saved.*
import org.jetbrains.anko.toast

/**
 * Fragment managing the books saved by the user.
 */
class SavedFragment : BookFragment() {

    companion object {
        private val TAG = SavedFragment::class.java.simpleName
    }

    override fun getFragmentLayout() = R.layout.fragment_saved

    override fun getBooksViewLayoutManager() = LinearLayoutManager(context)

    override fun getBooksView() : RecyclerView = savedBooksList

    override fun getBooksViewAdapter() = BookAdapter()

    override fun onScrollDownLoader() = requestOlderSavedBooks()

    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val latestBooks = BookManager.getSavedBooksList()
        (getBooksView().adapter as BookAdapter).addBooks(latestBooks)
    }

    /**
     * Function passed to the infinite scroll listener to load older saved books.
     *
     * It requests books saved by the user before the ones currently displayed.
     *
     * If older saved books are available they are added to the bottom of the list,
     * otherwise a message is shown and the loading item is removed.
     */
    private fun requestOlderSavedBooks() {
        val bookAdapter = getBooksView().adapter as BookAdapter

        if (BookManager.hasOlderSavedBooks()) {
            Log.i(TAG, "Requesting older saved books on scroll down")
            val olderBooks = BookManager.getOlderSavedBooks()
            bookAdapter.addBooks(olderBooks, addToTop = false)
        } else {
            Log.i(TAG, "No more older saved books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }
}