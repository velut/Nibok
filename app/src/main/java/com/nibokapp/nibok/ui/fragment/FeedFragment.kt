package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.getDpBasedLinearLayoutManager
import com.nibokapp.nibok.extension.startDetailActivity
import com.nibokapp.nibok.ui.adapter.InsertionAdapter
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
import com.nibokapp.nibok.ui.presenter.main.InsertionFeedPresenter
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
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
    }

    /*
     * Layout
     */

    override val layoutId: Int = R.layout.fragment_latest

    /*
     * Main view
     */

    override val mainView: RecyclerView by lazy { latestBooksList }

    override val mainAdapter: InsertionAdapter = InsertionAdapter(
            { context.startDetailActivity(it) },
            { context.startDetailActivity(it) },
            { Log.d(TAG, "Save click received") }
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
            { Log.d(TAG, "Search Save click received") }
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
}