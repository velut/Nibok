package com.nibokapp.nibok.ui.fragment

import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.BookModel
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

    override fun getMainViewAdapter() = BookAdapter { mainViewItemClickListener(it) }

    override fun getMainViewData(): List<ViewType> = BookManager.getSavedBooksList()

    override fun onMainViewScrollDownLoader() = requestOlderSavedBooks()

    override fun getSearchView(): RecyclerView = searchResultsList

    override fun getSearchViewLayoutManager() = LinearLayoutManager(context)

    override fun getSearchViewAdapter() = BookAdapter { searchViewItemClickListener(it) }

    override fun searchStrategy(query: String): List<ViewType> = BookManager.getBooksFromQuery(query)

    override fun hasMainViewUpdatableItems(): Boolean = true

    override fun hasMainViewRemovableItems(): Boolean = true

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
            bookAdapter.addBooks(olderBooks, addToBottom = true)
        } else {
            Log.i(TAG, "No more older saved books, end reached")
            bookAdapter.removeLoadingItem()
            context.toast(getString(R.string.end_reached))
        }
    }

    /**
     * Remove book insertions once unsaved, notify the user and offer the possibility to restore the
     * insertion after removal.
     *
     * @param item the item that was clicked
     */
    private fun mainViewItemClickListener(item: ViewType) {
        bookItemClickListener(item) {
            val book = it
            if (!book.saved) { // If book was removed
                val mainViewAdapter = getMainView().adapter as? BookAdapter
                mainViewAdapter?.let {

                    // Save old book position for possible reinsertion
                    val oldBookPosition = mainViewAdapter.removeBook(book)

                    // Notify user of removal
                    val snackBar = Snackbar.make(savedFragmentRoot,
                            R.string.book_removed_from_collection, Snackbar.LENGTH_LONG)

                    // Provide reinsertion possibility
                    snackBar.setAction(R.string.snackbar_undo_action) {
                        // Reinsert book if necessary
                        if (!UserManager.isInsertionSaved(book.insertionId)) {
                            UserManager.toggleSaveInsertion(book.insertionId)
                        }
                        book.saved = true
                        mainViewAdapter.addBooks(listOf(book), insertAtPosition = oldBookPosition)

                        // Notify the reinsertion
                        val childSnackBar = Snackbar.make(savedFragmentRoot,
                                R.string.book_reinserted_into_collection, Snackbar.LENGTH_SHORT)
                        childSnackBar.show()
                    }
                    snackBar.show()
                }
            }
        }
    }

    /**
     * Notify with toasts the user of the saved status of the clicked book insertion.
     *
     * @param item the item that was clicked
     */
    private fun searchViewItemClickListener(item: ViewType) {
        bookItemClickListener(item) {
            val toastMessage = if (it.saved) R.string.book_saved_to_collection
            else R.string.book_removed_from_collection
            context.toast(toastMessage)
        }
    }

    /**
     * Handle click on books.
     *
     * @param item the item that was clicked
     * @param onClick the function to execute given the clicked item
     */
    private fun bookItemClickListener(item: ViewType, onClick: (BookModel) -> Unit) {
        val book = item as? BookModel
        book?.let {
            Log.d(TAG, "Handling item click")
            onClick(it)
        }
    }
}