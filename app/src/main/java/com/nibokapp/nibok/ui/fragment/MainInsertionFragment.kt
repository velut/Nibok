package com.nibokapp.nibok.ui.fragment

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.ui.adapter.InsertionAdapter
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Abstract class representing fragments hosted by the main view displaying insertion related data.
 */
abstract class MainInsertionFragment : MainActivityFragment() {

    protected abstract val presenter: MainActivityPresenter<BookInsertionModel>


    /**
     * The adapter currently used by the main view safely casted as an InsertionAdapter.
     */
    protected val mainInsertionAdapter: InsertionAdapter?
        get() = mainView.adapter as? InsertionAdapter

    /**
     * The infinite scroll listener currently used by the main view.
     *
     * Updated every time a new one is built.
     */
    protected var mainInfiniteScrollListener: InfiniteScrollListener? = null

    /**
     * The adapter currently used by the search view safely casted as an InsertionAdapter.
     */
    protected val searchInsertionAdapter: InsertionAdapter?
        get() = searchView.adapter as? InsertionAdapter


    override fun addCachedData() {
        val adapter = mainInsertionAdapter ?: return
        adapter.items = presenter.getCachedData()
    }

    override fun updateData() {
        val adapter = mainInsertionAdapter ?: return
        doAsync {
            val data = presenter.getData()
            uiThread {
                adapter.items = data
                // MUST reset infinite scroll listener, otherwise it won't work with
                mainInfiniteScrollListener?.reset()
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return onQueryTextChange(query)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val adapter = searchInsertionAdapter ?: return false
        doAsync {
            val results = presenter.getQueryData(newText)
            Log.d(TAG, "Query results size: ${results.size}")
            uiThread {
                adapter.items = results
            }
        }
        return true
    }

    protected fun buildMainViewInfiniteScrollListener(): RecyclerView.OnScrollListener? {
        // Get the linear layout manager and the main adapter
        val llm = mainView.layoutManager as? LinearLayoutManager ?: return null
        val adapter = mainInsertionAdapter ?: return null
        // Update current listener reference
        mainInfiniteScrollListener = buildInfiniteScrollListener(llm, adapter)
        return mainInfiniteScrollListener
    }

    private fun buildInfiniteScrollListener(llm: LinearLayoutManager, adapter: InsertionAdapter): InfiniteScrollListener {
        return InfiniteScrollListener(llm) {
            val lastItem = adapter.items.lastOrNull()
            Log.d(TAG, "Loading insertion items older than: $lastItem")
            doAsync {
                val olderItems = if (lastItem != null) {
                    presenter.getDataOlderThanItem(lastItem)
                } else {
                    presenter.getData()
                }
                uiThread {
                    adapter.items += olderItems
                }
            }
        }
    }
}
