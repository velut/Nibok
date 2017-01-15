package com.nibokapp.nibok.ui.fragment.main

import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import com.nibokapp.nibok.ui.fragment.main.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.InsertionFeedPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Fragment managing the feed of books.
 *
 * It fetches the latest books published on the platform, it requests newer and older books.
 */
class LatestFragment(
        val presenter: ViewTypePresenter = InsertionFeedPresenter()
) : ViewTypeFragment() {

    companion object {
        private val TAG = LatestFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_latest

    override fun getFragmentName(): String = TAG

    // Presenter

    override fun getFragmentPresenter() = presenter

    // Main View

    override fun getMainViewId() = R.id.latestBooksList

    override fun getMainViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getMainViewAdapter() = ViewTypeAdapter(bookItemClickManager)

    // Main View Data

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = false

    // Search View

    override fun getSearchViewId() = R.id.searchResultsListLatest

    override fun getSearchViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getSearchViewAdapter() = ViewTypeAdapter(bookItemClickManager)

    override fun getSearchHint(): String = getString(R.string.search_hint_book)

    // Refresh

    override fun getRefreshUnsuccessfulString(): String = getString(R.string.no_newer_book_insertions)


    // Item click listener for the book cards
    private val bookItemClickManager = object : ViewTypeAdapter.ItemClickManager {

        override fun onButtonClick(itemId: String, itemType: Int) {

            if (itemType != ViewTypes.BOOK_INSERTION ||
                    presenter !is InsertionSaveStatusPresenter) {
                return
            }

            // Save button was clicked, save the insertion and alert user
            doAsync {
                val saved = presenter.toggleInsertionSave(itemId)
                uiThread {
                    val toastMessage = if (saved) R.string.book_saved_to_collection
                    else R.string.book_removed_from_collection
                    context.toast(toastMessage)
                }
            }
        }

        override fun showButton(): Boolean = true

        override fun updateItemOnButtonClick(): Boolean = true

        override fun onItemClick(itemId: String, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION)
                context.startDetailActivity(itemId)
        }
    }

}