package com.nibokapp.nibok.ui.fragment.main.common

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.nibokapp.nibok.ui.adapter.main.UpdatableAdapter
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
import com.nibokapp.nibok.ui.presenter.main.MainActivityPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Abstract class representing fragments hosted by the main view
 * displaying data held in an updatable adapter.
 * These fragments can optionally use infinite scroll when displaying their items.
 */
abstract class MainUpdatableAdapterFragment<T> : MainActivityFragment() {

    /**
     * Presenter used to operate on data.
     */
    protected abstract val presenter: MainActivityPresenter<T>

    /**
     * The updatable adapter currently used by the main view.
     */
    protected abstract val mainUpdatableAdapter: UpdatableAdapter<T>?

    /**
     * The optional infinite scroll listener used by the main view.
     * Updated every time a new one is built.
     */
    protected var mainInfiniteScrollListener: InfiniteScrollListener? = null

    /**
     * The updatable adapter currently used by the search view.
     */
    protected abstract val searchUpdatableAdapter: UpdatableAdapter<T>?


    override fun addCachedData() {
        val adapter = mainUpdatableAdapter ?: return
        Log.d(TAG, "Adding cached data")
        adapter.items = presenter.getCachedData()
    }

    override fun updateData() {
        val adapter = mainUpdatableAdapter ?: return
        doAsync {
            val data = presenter.getData()
            uiThread {
                Log.d(TAG, "Updating data")
                adapter.items = data
                // MUST reset infinite scroll listener, otherwise it won't work with new data
                mainInfiniteScrollListener?.reset()
            }
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return onQueryTextChange(query)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val adapter = searchUpdatableAdapter ?: return false
        Log.d(TAG, "Performing query for: $newText")
        doAsync {
            val results = presenter.getQueryData(newText)
            uiThread {
                Log.d(TAG, "Query results size: ${results.size} for: $newText")
                adapter.items = results
            }
        }
        return true
    }

    protected fun buildMainViewInfiniteScrollListener(): RecyclerView.OnScrollListener? {
        // Get the linear layout manager and the main adapter
        val llm = mainView?.layoutManager as? LinearLayoutManager ?: return null
        val adapter = mainUpdatableAdapter ?: return null
        // Update current listener reference
        mainInfiniteScrollListener = buildInfiniteScrollListener(llm, adapter)
        return mainInfiniteScrollListener
    }

    private fun buildInfiniteScrollListener(llm: LinearLayoutManager,
                                            adapter: UpdatableAdapter<T>): InfiniteScrollListener {
        return InfiniteScrollListener(llm) {
            val lastItem = adapter.items.lastOrNull()
            Log.d(TAG, "Loading items older than: $lastItem")
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
