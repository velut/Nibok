package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.delegate.BookDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.LoadingDelegateAdapter

/**
 * The adapter responsible for the overall book view.
 *
 * It delegates the managing of the items in the view to the respective adapters based on the
 * view type and adapter type.
 */
class BookAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListAdapter<ViewType> {

    // The loading item singleton
    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = AdapterTypes.LOADING
    }

    // Items to be displayed
    private val items = mutableListOf<ViewType>(loadingItem)

    // Adapter instances corresponding to adapter types
    private val delegateAdaptersMap = mapOf(
            AdapterTypes.LOADING to LoadingDelegateAdapter(),
            AdapterTypes.BOOK to BookDelegateAdapter()
    )


    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    /**
     * Delegate the creation of view holders to the right adapter given the viewType.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdaptersMap[viewType]!!.onCreateViewHolder(parent)
    }

    /**
     * Delegate the binding of view holders to the right adapter based on the viewType.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return delegateAdaptersMap[getItemViewType(position)]!!.onBindViewHolder(holder, items[position])
    }

    override fun addItems(items: List<ViewType>) = addBooks(items)

    override fun clearAndAddItems(items: List<ViewType>) = clearAndAddBooks(items)

    /**
     * Add books to the list of items to display.
     *
     * @param books the list of books to add
     * @param addToTop true if the books have to be added at the start of the list of items,
     * false if they are to be inserted at the end of the list (before the loading item). Default true.
     */
    fun addBooks(books: List<ViewType>, addToTop: Boolean = true) {
        val insertPosition = if (addToTop) 0 else itemCount - 1 // at top or at loading item position
        val insertItemCount = books.size

        items.addAll(insertPosition, books)
        notifyItemRangeInserted(insertPosition, insertItemCount)
    }

    /**
     * Clear the items list and add the given books.
     *
     * @param books the books to add to the items list
     */
    fun clearAndAddBooks(books: List<ViewType>) {
        val oldItemCount = itemCount
        items.clear()
        notifyItemRangeRemoved(0, oldItemCount)
        addBooks(books)
    }

    /**
     * Remove the loading item from the list of items to be displayed.
     */
    fun removeLoadingItem() {
        if (items.contains(loadingItem)) {
            val loadingItemPosition = items.indexOf(loadingItem)
            items.remove(loadingItem)
            notifyItemRemoved(loadingItemPosition)
        }
    }

}