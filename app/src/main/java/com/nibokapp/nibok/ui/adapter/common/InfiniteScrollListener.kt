package com.nibokapp.nibok.ui.adapter.common

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Scroll listener that enables infinite scrolling.
 *
 * When the view is scrolled down if not enough items are present it loads more by calling the given
 * loading function.
 * If enough items are present it stops loading.
 *
 * @param layoutManager the layout manager
 * @param loadFunc the function to be called to load more items
 */
class InfiniteScrollListener(val layoutManager: LinearLayoutManager, val loadFunc: () -> Unit) :
        RecyclerView.OnScrollListener() {

    private val VISIBLE_THRESHOLD = 3

    private var loading = true
    private var previousTotal = 0
    private var totalItemCount = 0
    private var visibleItemCount = 0
    private var firstVisibleItemPosition = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Scrolled down
        if (dy > 0) {
            totalItemCount = layoutManager.itemCount
            visibleItemCount = layoutManager.childCount
            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            // There are enough items, stop loading
            if (loading && (totalItemCount > previousTotal)) {
                loading = false
                previousTotal = totalItemCount
            }

            // Almost reached the end, load more items
            if (!loading && isThresholdReached()) {
                loadFunc()
                loading = true
            }
        }
    }

    private fun isThresholdReached() =
            (totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + VISIBLE_THRESHOLD)

}