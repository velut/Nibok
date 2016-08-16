package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.adapter.BookAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import kotlinx.android.synthetic.main.fragment_saved.*
import org.jetbrains.anko.toast

/**
 * Fragment managing the books saved by the user.
 */
class SavedFragment : ViewTypeFragment() {

    companion object {
        private val TAG = SavedFragment::class.java.simpleName
    }

    override fun getFragmentLayout() = R.layout.fragment_saved

    override fun getMainView() : RecyclerView = savedBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = BookAdapter()

    override fun getMainViewData(): List<ViewType> = BookManager.getSavedBooksList()

    override fun onMainViewScrollDownLoader() = requestOlderSavedBooks()

    override fun getSearchView(): RecyclerView = searchResultsList

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = BookAdapter()

    override fun searchStrategy(query: String): List<ViewType> = BookManager.getBooksFromQuery(query)

    override fun hasUpdatableData(): Boolean = true

    override fun hasRemovableData(): Boolean = true

    override fun getFragmentName() : String = TAG


    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
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
        val bookAdapter = getMainView().adapter as BookAdapter

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