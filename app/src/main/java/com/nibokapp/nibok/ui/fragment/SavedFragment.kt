package com.nibokapp.nibok.ui.fragment

import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.adapter.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewTypes
import com.nibokapp.nibok.ui.fragment.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.SavedInsertionPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import kotlinx.android.synthetic.main.fragment_saved.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Fragment managing the books saved by the user.
 */
class SavedFragment(val presenter: ViewTypePresenter = SavedInsertionPresenter()) :
        ViewTypeFragment() {

    companion object {
        private val TAG = SavedFragment::class.java.simpleName
    }

    // Fragment

    override fun getFragmentLayout() = R.layout.fragment_saved

    override fun getFragmentName() : String = TAG

    // Presenter

    override fun getFragmentPresenter() = presenter

    // Main View

    override fun getMainView() : RecyclerView = savedBooksList

    override fun getMainViewId() = R.id.savedBooksList

    override fun getMainViewLayoutManager() = LinearLayoutManager(context)

    override fun getMainViewAdapter() = ViewTypeAdapter(mainViewBookItemClickListener)

    // Main View Data

    override fun onMainViewScrollDownLoader() = requestOlderSavedBooks()

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = true

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListSaved

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = ViewTypeAdapter(searchViewBookItemClickListener)

    override fun getSearchHint() : String = getString(R.string.search_hint_book)


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
        val bookAdapter = getMainView().adapter as ViewTypeAdapter

        if (BookManager.hasOlderSavedBooks()) {
            Log.i(TAG, "Requesting older saved books on scroll down")
            val olderBooks = BookManager.getOlderSavedBooks()
            bookAdapter.addItems(olderBooks, insertAtBottom = true)
        } else {
            Log.i(TAG, "No more older saved books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }

    private val mainViewBookItemClickListener = object : ViewTypeAdapter.ItemClickListener {
        override fun onButtonClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK) mainViewItemClickListener(itemId, itemType)
        }

        override fun onItemClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK) startDetailActivity(itemId)
        }
    }

    private val searchViewBookItemClickListener = object : ViewTypeAdapter.ItemClickListener {
        override fun onButtonClick(itemId: Long, itemType: Int) {

            if (itemType != ViewTypes.BOOK || presenter !is InsertionSaveStatusPresenter) return

            val saved = presenter.toggleInsertionSave(itemId)
            val toastMessage = if (saved) R.string.book_saved_to_collection
                                else R.string.book_removed_from_collection
            context.toast(toastMessage)
        }

        override fun onItemClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK) startDetailActivity(itemId)
        }
    }

    /**
     * Start the detail activity about the given insertion.
     */
    private fun startDetailActivity(itemId: Long) =
            context.startActivity<InsertionDetailActivity>(
                    InsertionDetailFragment.INSERTION_ID to itemId)

    /**
     * Remove book insertions once unsaved, notify the user and offer the possibility to restore the
     * insertion after removal.
     *
     * @param itemId the id of the item that was clicked
     * @param itemType the type of the item that was clicked
     */
    private fun mainViewItemClickListener(itemId: Long, itemType: Int) {

        if (presenter !is InsertionSaveStatusPresenter) return

        val saved = presenter.toggleInsertionSave(itemId)
        val mainViewAdapter = getMainView().adapter as? ViewTypeAdapter

        if (saved || mainViewAdapter == null) return

        // Remove the book

        // Save old book position for possible reinsertion
        val oldBookPosition = mainViewAdapter.removeItemById(itemId, itemType)
        refreshMainViewData() // Book was removed, sync fragment data

        // Notify user of removal
        val snackBar = Snackbar.make(savedFragmentRoot,
                R.string.book_removed_from_collection, Snackbar.LENGTH_LONG)

        // Provide reinsertion possibility
        snackBar.setAction(R.string.snackbar_undo_action) {
            // Reinsert book if necessary
            if (!presenter.isInsertionSaved(itemId)) {
                presenter.toggleInsertionSave(itemId)
                mainViewAdapter.restoreItemById(itemId, itemType, position = oldBookPosition)
                checkForUpdates() // Sync fragment data and view
                // Notify the reinsertion
                val childSnackBar = Snackbar.make(savedFragmentRoot,
                        R.string.book_reinserted_into_collection, Snackbar.LENGTH_SHORT)
                childSnackBar.show()
            } else {
                // Book was reinserted from other pages before the undo operation
                val childSnackBar = Snackbar.make(savedFragmentRoot,
                        R.string.book_already_into_collection, Snackbar.LENGTH_SHORT)
                childSnackBar.show()
            }
        }
        snackBar.show()
    }
}