package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.domain.model.MessageModel
import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.delegate.BookDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.LoadingDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.MessageDelegateAdapter

/**
 * The adapter responsible for managing and displaying ViewType items.
 *
 * This adapter delegates the managing of subclasses of ViewType items to its delegate adapters
 * based on a correspondence (view type -> delegate adapter).
 *
 * @param itemClickListener function to be called if an item is clicked. Optional
 */
class ViewTypeAdapter(itemClickListener: (ViewType) -> Unit =
                      { Log.d(ViewTypeAdapter.TAG, "Item clicked") })
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListAdapter<ViewType> {

    companion object {
        private val TAG = ViewTypeAdapter::class.java.simpleName
    }

    // The loading item object
    private val loadingItem = object : ViewType {
        override fun getViewType(): Int = AdapterTypes.LOADING
    }

    // Items to be displayed
    private val items = mutableListOf<ViewType>(loadingItem)

    // Adapter instances corresponding to adapter types
    private val delegateAdaptersMap = mapOf(
            AdapterTypes.LOADING to LoadingDelegateAdapter(),
            AdapterTypes.BOOK to BookDelegateAdapter(itemClickListener),
            AdapterTypes.MESSAGE to MessageDelegateAdapter()
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

    override fun addItems(items: List<ViewType>) = addViewTypeItems(items)

    override fun clearAndAddItems(items: List<ViewType>) = clearAndAddViewTypeItems(items)

    override fun updateItems(items: List<ViewType>) = updateBooks(items)

    override fun removeItems(items: List<ViewType>) = removeBooks(items)

    /**
     * Add given items to the list of items to display.
     *
     * @param items the list of items to add
     * @param insertAtPosition the desired position at which items are inserted. Default = 0 (top)
     * @param addToBottom true if the items have to be added at the end of the list of items,
     * false if they are to be inserted at the specified position. Default = false.
     * @param preventDuplicates true if items with ids already present in the current items
     * should not be added, false if items with the same id are allowed to be in the items list
     */
    fun addViewTypeItems(items: List<ViewType>, insertAtPosition : Int = 0,
                         addToBottom: Boolean = false, preventDuplicates: Boolean = true) {

        if (items.isEmpty()) return

        var itemsToAdd: List<ViewType> = items

        if (preventDuplicates) {
            var booksToAdd: List<BookModel> = items.filterIsInstance<BookModel>()
            var messagesToAdd: List<MessageModel> = items.filterIsInstance<MessageModel>()

            booksToAdd = booksToAdd.filter { it.insertionId !in getCurrentBookIds() }
            messagesToAdd = messagesToAdd.filter { it.conversationId !in getCurrentMessageIds() }

            itemsToAdd = booksToAdd + messagesToAdd
        }

        if (itemsToAdd.isNotEmpty()) {
            val insertPosition = if (addToBottom) itemCount - 1 else  insertAtPosition
            val insertItemCount = itemsToAdd.size
            this.items.addAll(insertPosition, itemsToAdd)
            notifyItemRangeInserted(insertPosition, insertItemCount)
            Log.d(TAG, "Added $insertItemCount items at position $insertPosition")
        }
    }

    /**
     * Clear the items list and add the given items.
     *
     * @param items the items to add to the items list
     */
    fun clearAndAddViewTypeItems(items: List<ViewType>) {
        val oldItemCount = itemCount
        this.items.clear()
        notifyItemRangeRemoved(0, oldItemCount)
        Log.d(TAG, "Cleared items")
        addViewTypeItems(items)
    }

    /**
     * Update the current list of items with the new books.
     * A book already present in the current list is updated, a new book is inserted into the list.
     *
     * @param books the list of books to add or update in the current list of displayed items
     */
    fun updateBooks(books: List<ViewType>) {
        val updatedBooks = castItemsToBooks(books)
        val currentBookIds = getCurrentBookIds()
        updatedBooks?.let {
            val (toReplace, toAdd) = it.partition { it.insertionId in currentBookIds }
            Log.d(TAG, "To replace: ${toReplace.size}; to add: ${toAdd.size}")
            replaceBooks(toReplace)
            addViewTypeItems(toAdd)
        }
    }

    /**
     * Replace items in the current list with the given ones with the same id.
     *
     * @param newBooks the new version of the current items to update
     */
    fun replaceBooks(newBooks: List<BookModel>) = newBooks.forEach { replaceBook(it) }

    /**
     * Replace a book with a given id with the new version if the ids correspond.
     *
     * @param newBook the new version of the current book to update
     */
    fun replaceBook(newBook: BookModel) {
        val currentBooks = getCurrentBookItems()
        val bookToReplace = currentBooks.find { it.insertionId == newBook.insertionId }
        bookToReplace?.let {
            Log.d(TAG, "Replacing book with id: ${bookToReplace.insertionId}")
            val bookIndex = items.indexOf(bookToReplace)
            items[bookIndex] = newBook
            notifyItemChanged(bookIndex)
        }
    }

    /**
     * Remove a list of items from the current items.
     *
     * @param books the list of items to remove
     */
    fun removeBooks(books: List<ViewType>) {
        val booksToRemove = castItemsToBooks(books)
        Log.d(TAG, "To remove: ${booksToRemove?.size}")
        booksToRemove?.forEach { removeBook(it) }
    }

    /**
     * Remove a book with a given id with the new version if the ids correspond.
     *
     * @param book the book to remove
     *
     * @return the position in which the book was before being removed or -1 if no book was removed
     */
    fun removeBook(book: BookModel) : Int {
        val currentBooks = getCurrentBookItems()
        val bookToRemove = currentBooks.find { it.insertionId == book.insertionId }
        var bookIndex: Int = -1
        bookToRemove?.let {
            Log.d(TAG, "Removing book with id: ${bookToRemove.insertionId}")
            bookIndex = items.indexOf(bookToRemove)
            items.removeAt(bookIndex)
            notifyItemRemoved(bookIndex)
        }
        return bookIndex
    }

    /**
     * Get only the book items present in the adapter's items list.
     */
    private fun getCurrentBookItems() : List<BookModel> = items.filterIsInstance<BookModel>()

    /**
     * Get the ids of the books present in the items list.
     */
    private fun getCurrentBookIds() = getCurrentBookItems().map { it.insertionId }

    /**
     * Get only the message items present in the adapter's item list.
     */
    private fun getCurrentMessageItems() : List<MessageModel> = items.filterIsInstance<MessageModel>()

    /**
     * Get the ids of the messages present in the items list.
     */
    private fun getCurrentMessageIds() = getCurrentMessageItems().map { it.conversationId }

    private fun castItemsToBooks(items: List<ViewType>) : List<BookModel>? {
        @Suppress("UNCHECKED_CAST")
        val result = items as? List<BookModel>
        return result
    }

    /**
     * Remove the loading item from the list of items to be displayed.
     */
    fun removeLoadingItem() {
        val loadingItemPosition = items.indexOf(loadingItem)
        if (loadingItemPosition != -1) {
            items.removeAt(loadingItemPosition)
            notifyItemRemoved(loadingItemPosition)
            Log.d(TAG, "Removed loading item")
        }
    }

}