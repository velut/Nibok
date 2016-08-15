package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import kotlinx.android.synthetic.main.fragment_latest.*
import org.jetbrains.anko.toast

/**
 * Fragment managing the feed of books.
 *
 * It fetches the latest books published on the platform, it requests newer and older books.
 */
class LatestFragment : ViewTypeFragment() {

    companion object {
        private val TAG = LatestFragment::class.java.simpleName
    }

    override fun getFragmentLayout() = R.layout.fragment_latest

    override fun getMainView() : RecyclerView = latestBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = BookAdapter()

    override fun getMainViewData(): List<ViewType> = BookManager.getFeedBooksList()

    override fun onMainViewScrollDownLoader() = requestOlderBooks()

    override fun getSearchView(): RecyclerView = searchResultsList

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = BookAdapter()

    override fun searchStrategy(query: String): List<ViewType> = BookManager.getBooksFromQuery(query)

    override fun hasUpdatableData(): Boolean = true

    override fun hasRemovableData(): Boolean = false

    override fun getFragmentName() : String = TAG


    override fun handleRefreshAction() {
        // If new books are available add them to the list and return to the top
        if (BookManager.hasNewerFeedBooks()) {
            val newerBooks = BookManager.getNewerFeedBooks()
            (latestBooksList.adapter as BookAdapter).addBooks(newerBooks)
            handleBackToTopAction()
        } else {
            context.toast(getString(R.string.no_newer_books))
        }
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

        if (BookManager.hasOlderFeedBooks()) {
            Log.d(TAG, "Requesting older books on scroll down")
            val olderBooks = BookManager.getOlderFeedBooks()
            bookAdapter.addBooks(olderBooks, addToTop = false)
        } else {
            Log.d(TAG, "No more older books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }

}