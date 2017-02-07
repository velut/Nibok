package com.nibokapp.nibok.ui.fragment

import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.adapter.InsertionAdapter
import com.nibokapp.nibok.ui.presenter.main.InsertionBookmarkPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import kotlinx.android.synthetic.main.fragment_saved.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * MainActivityFragment used to display the insertions saved by the user.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class BookmarkFragment(
        override val presenter: MainActivityPresenter<BookInsertionModel> = InsertionBookmarkPresenter()
) : MainInsertionFragment() {

    companion object {
        private val TAG = BookmarkFragment::class.java.simpleName
    }

    override val TAG: String = BookmarkFragment.TAG

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_saved

    /*
     * Presenter
     */

    private val savePresenter: InsertionSaveStatusPresenter? by lazy {
        presenter as? InsertionSaveStatusPresenter
    }

    /*
     * Main view
     */

    override val mainViewId: Int = R.id.savedBooksList

    override val mainAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { toggleInsertionSaveStatus(it) }
    )

    override val mainLayoutManager: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()

    override val mainScrollListener: RecyclerView.OnScrollListener?
        get() = buildMainViewInfiniteScrollListener()

    /*
     * Search view
     */

    override val searchViewId: Int = R.id.searchResultsListSaved

    override val searchAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { toggleInsertionSaveStatus(it) }
    )

    override val searchLayoutManger: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()


    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_book) }


    private fun toggleInsertionSaveStatus(insertionId: String) {

        if (!isUserLoggedIn()) return

        val presenter = savePresenter ?: return

        doAsync {
            val isSaved = presenter.toggleInsertionSave(insertionId)
            uiThread {
                if (!isSaved) {
                    val oldMainItems = mainAdapter.removeInsertion(insertionId)
                    val oldSearchItems = searchAdapter.removeInsertion(insertionId)
                    Log.d(TAG, "Insertion: $insertionId removed, showing restore option")
                    showRestoreOption(presenter, insertionId, oldMainItems, oldSearchItems)
                }
            }
        }
    }

    private fun showRestoreOption(presenter: InsertionSaveStatusPresenter, insertionId: String,
                                  oldMainItems: List<BookInsertionModel>, oldSearchItems: List<BookInsertionModel>) {
        // Alert user that insertion was removed
        val snackbar = Snackbar.make(savedFragmentRoot,
                R.string.book_removed_from_collection, Snackbar.LENGTH_LONG)

        // Provide a way to restore insertion
        snackbar.setAction(R.string.snackbar_undo_action) {
            tryRestoreInsertion(presenter, insertionId, oldMainItems, oldSearchItems)
        }
        snackbar.show()
    }

    private fun tryRestoreInsertion(presenter: InsertionSaveStatusPresenter, insertionId: String,
                                    oldMainItems: List<BookInsertionModel>, oldSearchItems: List<BookInsertionModel>) {
        val isCurrentlySaved = presenter.isInsertionSaved(insertionId)
        if (!isCurrentlySaved) {
            restoreInsertion(presenter, insertionId, oldMainItems, oldSearchItems)
        } else {
            showAlreadyRestored()
        }
    }

    private fun restoreInsertion(presenter: InsertionSaveStatusPresenter, insertionId: String,
                                 oldMainItems: List<BookInsertionModel>, oldSearchItems: List<BookInsertionModel>) {
        doAsync {
            val isSaved = presenter.toggleInsertionSave(insertionId)
            uiThread {
                showRestoreResult(isSaved)
                if (isSaved) {
                    mainAdapter.items = oldMainItems
                    searchAdapter.items = oldSearchItems
                    Log.d(TAG, "Restored insertion: $insertionId")
                }
            }
        }
    }

    private fun showAlreadyRestored() {
        showSnackBar(R.string.book_already_into_collection)
    }

    private fun showRestoreResult(isSaved: Boolean) {
        val resultMessage = if (isSaved) {
            R.string.book_reinserted_into_collection
        } else {
            R.string.book_reinsert_error
        }
        showSnackBar(resultMessage)
    }

    private fun showSnackBar(messageResId: Int) {
        val snackbar = Snackbar.make(savedFragmentRoot, messageResId, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }
}