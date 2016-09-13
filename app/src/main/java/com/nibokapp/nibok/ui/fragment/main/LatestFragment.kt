package com.nibokapp.nibok.ui.fragment.main

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import com.nibokapp.nibok.ui.fragment.InsertionDetailFragment
import com.nibokapp.nibok.ui.fragment.main.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.InsertionFeedPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import kotlinx.android.synthetic.main.fragment_latest.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Fragment managing the feed of books.
 *
 * It fetches the latest books published on the platform, it requests newer and older books.
 */
class LatestFragment(val presenter: ViewTypePresenter = InsertionFeedPresenter()) :
        ViewTypeFragment() {

    companion object {
        private val TAG = LatestFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_latest

    override fun getFragmentName() : String = TAG

    // Presenter

    override fun getFragmentPresenter() = presenter

    // Main View

    override fun getMainView() : RecyclerView = latestBooksList

    override fun getMainViewId() = R.id.latestBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter(bookItemClickListener)

    // Main View Data

    override fun onMainViewScrollDownLoader() = requestOlderBooks()

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListLatest

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = ViewTypeAdapter(bookItemClickListener)

    override fun getSearchHint() : String = getString(R.string.search_hint_book)


    override fun handleRefreshAction() {
        // TODO Use presenter
        // If new books are available add them to the list and return to the top
        if (BookManager.hasNewerFeedBooks()) {
            /*
            val newerBooks = BookManager.getNewerFeedBooks()
            (latestBooksList.adapter as ViewTypeAdapter).addItems(newerBooks)*/
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
        val bookAdapter = latestBooksList.adapter as ViewTypeAdapter

        if (BookManager.hasOlderFeedBooks()) {
            Log.d(TAG, "Requesting older books on scroll down")
            /*val olderBooks = BookManager.getOlderFeedBooks()
            bookAdapter.addItems(olderBooks, insertAtBottom = true)*/
        } else {
            Log.d(TAG, "No more older books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }

    // Item click listener for the book cards
    private val bookItemClickListener = object : ViewTypeAdapter.ItemClickListener {

        override fun onButtonClick(itemId: Long, itemType: Int) {

            if (itemType != ViewTypes.BOOK_INSERTION ||
                    presenter !is InsertionSaveStatusPresenter) {
                return
            }

            // Save button was clicked, save the insertion and alert user
            val saved = presenter.toggleInsertionSave(itemId)
            val toastMessage = if (saved) R.string.book_saved_to_collection
                                else R.string.book_removed_from_collection
            context.toast(toastMessage)
        }

        override fun updateItemOnButtonClick(): Boolean = true

        override fun onItemClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION) startDetailActivity(itemId)
        }
    }

    /**
     * Start the detail activity about the given insertion.
     */
    private fun startDetailActivity(itemId: Long) =
            context.startActivity<InsertionDetailActivity>(
                    InsertionDetailFragment.INSERTION_ID to itemId)

}