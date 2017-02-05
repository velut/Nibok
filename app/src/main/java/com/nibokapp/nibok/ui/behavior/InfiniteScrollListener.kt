package com.nibokapp.nibok.ui.behavior

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Scroll listener that enables infinite scrolling.
 *
 * When the view is scrolled down if not enough items are present it loads more by calling the given
 * loading function.
 * If enough items are present it stops loading.
 *
 * @param layoutManager the layout manager
 * @param loadOnScrollDown true if the listener should listen to scroll down events,
 *                         false for scroll up events. Default is true
 * @param bufferSize the minimum size of the buffer,
 *                   if the current buffer contains less items than bufferSize new items are loaded.
 *                   Default buffer size is 10
 * @param loadFunc the function to be called to load more items
 */
class InfiniteScrollListener(val layoutManager: LinearLayoutManager,
                             val loadOnScrollDown: Boolean = true,
                             val bufferSize: Int = 10,
                             val loadFunc: () -> Unit) :
        RecyclerView.OnScrollListener() {

    companion object {
        private val TAG: String = InfiniteScrollListener::class.java.simpleName

        private val VERBOSE: Boolean = false
    }

    private var loading = true
    private var previousTotal = 0
    private var totalItemCount = 0
    private var newestVisibleItemPosition = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val hasScrolled = if (loadOnScrollDown) dy > 0 else dy < 0

        if (hasScrolled) {
            // Total items contained in the adapter bound to the RecyclerView
            totalItemCount = layoutManager.itemCount

            // The newest visible item is the one that just entered the view while scrolling
            // Thus it is the last visible item (bottom) when scrolling down
            // and the first visible item (top) when scrolling up
            newestVisibleItemPosition = if (loadOnScrollDown) {
                layoutManager.findLastVisibleItemPosition()
            } else {
                layoutManager.findFirstVisibleItemPosition()
            }

            if (VERBOSE) {
                Log.d(TAG, "User has scrolled")
                Log.d(TAG, "Total item count: $totalItemCount")
                Log.d(TAG, "Previous item count: $previousTotal")
                Log.d(TAG, "Newest visible item position: $newestVisibleItemPosition")
                Log.d(TAG, "Loading: $loading")
            }

            if (loading && (totalItemCount > previousTotal)) {
                if (VERBOSE) Log.d(TAG, "Enough items, stop loading")
                loading = false
                previousTotal = totalItemCount
            }

            if (!loading && isThresholdReached()) {
                if (VERBOSE) Log.d(TAG, "Loading more items")
                loadFunc()
                loading = true
            }
        }
    }

    private fun isThresholdReached(): Boolean {
        val scrollBufferItemCount = if (loadOnScrollDown) {
            totalItemCount - (newestVisibleItemPosition + 1)
        } else {
            newestVisibleItemPosition
        }
        return scrollBufferItemCount <= bufferSize
    }

    fun reset() {
        if (VERBOSE) Log.d(TAG, "Resetting $TAG")
        loading = true
        previousTotal = 0
        totalItemCount = 0
        newestVisibleItemPosition = 0
    }
}