package com.nibokapp.nibok.ui.fragment

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.activity.AuthenticateActivity
import com.nibokapp.nibok.ui.adapter.InsertionAdapter
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
import com.nibokapp.nibok.ui.presenter.main.InsertionFeedPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import com.nibokapp.nibok.ui.presenter.viewtype.common.InsertionSaveStatusPresenter
import kotlinx.android.synthetic.main.fragment_latest.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * MainActivityFragment used to display the feed containing the latest insertions.
 *
 * @param presenter the presenter used by this fragment to perform data operations
 */
class FeedFragment(
        val presenter: MainActivityPresenter<BookInsertionModel> = InsertionFeedPresenter()
) : MainActivityFragment() {

    companion object {
        private val TAG = FeedFragment::class.java.simpleName

        private val KEY_INSERTION_TO_TOGGLE = "$TAG:insertionToToggle"
    }

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_latest

    /*
     * Presenter
     */

    private val savePresenter: InsertionSaveStatusPresenter? by lazy {
        presenter as? InsertionSaveStatusPresenter
    }

    /*
     * Main view
     */

    override val mainView: RecyclerView by lazy { latestBooksList }

    override val mainAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { toggleInsertionSaveStatus(it) }
    )

    override val mainLayoutManager: LinearLayoutManager by lazy {
        context.getDpBasedLinearLayoutManager()
    }

    override val mainScrollListener: RecyclerView.OnScrollListener? by lazy {
        InfiniteScrollListener(mainLayoutManager) {
            val lastItem = mainAdapter.items.lastOrNull()
            Log.d(TAG, "Loading feed items older than: $lastItem")
            doAsync {
                val olderItems = if (lastItem != null) {
                    presenter.getDataOlderThanItem(lastItem)
                } else {
                    presenter.getData()
                }
                uiThread {
                    mainAdapter.items += olderItems
                }
            }
        }
    }

    /*
     * Search view
     */

    override val searchView: RecyclerView by lazy { searchResultsListLatest }

    override val searchAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { TODO() }
    )

    override val searchLayoutManger: LinearLayoutManager by lazy {
        context.getDpBasedLinearLayoutManager()
    }

    override val searchScrollListener: RecyclerView.OnScrollListener? = null

    override val searchHint: String by lazy { getString(R.string.search_hint_book) }

    /*
     * Data handling
     */

    override fun addCachedData() {
        mainAdapter.items = presenter.getCachedData()
    }

    override fun updateData() {
        doAsync {
            val data = presenter.getData()
            uiThread {
                mainAdapter.items = data
                val infiniteScrollListener = mainScrollListener as? InfiniteScrollListener
                infiniteScrollListener?.reset()
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return onQueryTextChange(query)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        doAsync {
            val results = presenter.getQueryData(newText)
            Log.d(TAG, "Query results size: ${results.size}")
            uiThread {
                searchAdapter.items = results
            }
        }
        return true
    }

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
            val isSaved = presenter.toggleInsertionSave(insertionId)
            uiThread {
                val items = mainAdapter.items.map {
                    if (it.insertionId == insertionId) {
                        it.copy(savedByUser = isSaved)
                    } else {
                        it
                    }
                }
                mainAdapter.items = items
            }
        }
    }
}