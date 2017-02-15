package com.nibokapp.nibok.ui.fragment.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.activity.AuthenticateActivity
import com.nibokapp.nibok.ui.adapter.main.InsertionAdapter
import com.nibokapp.nibok.ui.adapter.main.UpdatableAdapter
import com.nibokapp.nibok.ui.fragment.main.common.MainUpdatableAdapterFragment
import com.nibokapp.nibok.ui.presenter.main.InsertionFeedPresenter
import com.nibokapp.nibok.ui.presenter.main.InsertionSaveStatusPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


/**
 * MainActivityFragment used to display the feed containing the latest insertions.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class FeedFragment(
        override val presenter: MainActivityPresenter<BookInsertionModel> = InsertionFeedPresenter()
) : MainUpdatableAdapterFragment<BookInsertionModel>() {

    companion object {
        private val TAG = FeedFragment::class.java.simpleName

        private val KEY_INSERTION_TO_TOGGLE = "$TAG:insertionToToggle"
    }

    override val TAG: String = Companion.TAG

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_feed

    /*
     * Presenter
     */

    private val savePresenter: InsertionSaveStatusPresenter? by lazy {
        presenter as? InsertionSaveStatusPresenter
    }

    /*
     * Main view
     */

    override val mainViewId: Int = R.id.feedList

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

    override val searchViewId: Int = R.id.searchFeedList

    override val searchAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { toggleInsertionSaveStatus(it) }
    )

    override val searchLayoutManger: LinearLayoutManager
        get() = context.getDpBasedLinearLayoutManager()

    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_book) }

    override val noSearchResultsHintId: Int = R.string.search_hint_no_insertions

    /*
     * Updatable adapters
     */

    override val mainUpdatableAdapter: UpdatableAdapter<BookInsertionModel>? = mainAdapter

    override val searchUpdatableAdapter: UpdatableAdapter<BookInsertionModel>? = searchAdapter


    override fun onSuccessfulAuthResult(data: Intent?) {
        if (data == null) return
        Log.d(TAG, "User authenticated successfully")
        val insertionId = data.getStringExtra(KEY_INSERTION_TO_TOGGLE) ?: return
        saveInsertion(insertionId)
    }

    private fun saveInsertion(insertionId: String) {
        val presenter = savePresenter ?: return
        doAsync {
            val isSaved = presenter.isInsertionSaved(insertionId)
            uiThread {
                if (!isSaved) {
                    toggleInsertionSaveStatus(insertionId)
                }
            }
        }
    }

    private fun toggleInsertionSaveStatus(insertionId: String) {

        if (!isUserLoggedIn()) {
            Log.d(TAG, "User needs to login before saving insertion")
            val intent = Intent(context, AuthenticateActivity::class.java)
            intent.putExtra(KEY_INSERTION_TO_TOGGLE, insertionId)
            startActivityForResult(intent, REQUEST_AUTHENTICATE)
            return
        }

        Log.d(TAG, "Toggling save status for insertion: $insertionId")
        val presenter = savePresenter ?: return
        doAsync {
            val wasSaved = presenter.isInsertionSaved(insertionId)
            val isSaved = presenter.toggleInsertionSave(insertionId)
            uiThread {
                if (wasSaved != isSaved) { // Changed status successfully
                    mainAdapter.toggleInsertionSaveStatus(insertionId, isSaved)
                    searchAdapter.toggleInsertionSaveStatus(insertionId, isSaved)
                    val saveMessage = if (isSaved) {
                        R.string.book_saved_to_collection
                    } else {
                        R.string.book_removed_from_collection
                    }
                    context.toast(saveMessage)
                } else {
                    Log.d(TAG, "Could not change save status")
                    val errorMessage = if (wasSaved) {
                        R.string.error_could_not_remove_bookmark
                    } else {
                        R.string.error_could_not_add_bookmark
                    }
                    context.toast(errorMessage)
                }

            }
        }
    }
}