package com.nibokapp.nibok.ui.fragment.main

import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import com.nibokapp.nibok.ui.fragment.main.common.ViewTypeFragment
import com.nibokapp.nibok.ui.presenter.viewtype.SavedInsertionPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import kotlinx.android.synthetic.main.fragment_saved.*
import org.jetbrains.anko.toast

/**
 * Fragment managing the books saved by the user.
 */
class SavedFragment(
        val presenter: ViewTypePresenter = SavedInsertionPresenter()
) :
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

    override fun getMainViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getMainViewAdapter() = ViewTypeAdapter(mainViewBookItemClickManager)

    // Main View Data

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = true

    // Search View

    override fun getSearchView(): RecyclerView = searchResultsListSaved

    override fun getSearchViewLayoutManager() = context.getDpBasedLinearLayoutManager()

    override fun getSearchViewAdapter() = ViewTypeAdapter(searchViewBookItemClickManager)

    override fun getSearchHint() : String = getString(R.string.search_hint_book)

    // Refresh

    override fun getNoNewerItemsFromRefreshString(): String = getString(R.string.no_newer_book_insertions)

    private val mainViewBookItemClickManager = object : ViewTypeAdapter.ItemClickManager {
        override fun onButtonClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION) mainViewItemClickListener(itemId, itemType)
        }

        override fun showButton(): Boolean = true

        override fun updateItemOnButtonClick(): Boolean = false

        override fun onItemClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION)
                context.startDetailActivity(itemId)
        }
    }

    private val searchViewBookItemClickManager = object : ViewTypeAdapter.ItemClickManager {
        override fun onButtonClick(itemId: Long, itemType: Int) {

            if (itemType != ViewTypes.BOOK_INSERTION ||
                    presenter !is InsertionSaveStatusPresenter) {
                return
            }

            val saved = presenter.toggleInsertionSave(itemId)
            val toastMessage = if (saved) R.string.book_saved_to_collection
                                else R.string.book_removed_from_collection
            context.toast(toastMessage)
        }

        override fun showButton(): Boolean = true

        override fun updateItemOnButtonClick(): Boolean = true

        override fun onItemClick(itemId: Long, itemType: Int) {
            if (itemType == ViewTypes.BOOK_INSERTION)
                context.startDetailActivity(itemId)
        }
    }

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