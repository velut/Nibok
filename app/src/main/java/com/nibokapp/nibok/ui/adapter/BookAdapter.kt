package com.nibokapp.nibok.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.delegate.BookDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.LoadingDelegateAdapter
import com.nibokapp.nibok.ui.adapter.delegate.MessageDelegateAdapter

/**
 * The adapter responsible for the overall book view.
 *
 * It delegates the managing of the items in the view to the respective adapters based on the
 * view type and adapter type.
 */
class BookAdapter(itemClickListener: (ViewType) -> Unit = { Log.d(TAG, "Item clicked")})
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListAdapter<ViewType> {

    companion object {
        private val TAG = BookAdapter::class.java.simpleName
    }

    // The loading item singleton
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

    override fun addItems(items: List<ViewType>) = addBooks(items)

    override fun clearAndAddItems(items: List<ViewType>) = clearAndAddBooks(items)

    override fun updateItems(items: List<ViewType>) = updateBooks(items)

    override fun removeItems(items: List<ViewType>) = removeBooks(items)

    /**
     * Add books to the list of items to display.
     *
     * @param books the list of books to add
     * @param insertAtPosition the desired position at which books are inserted. Default = 0 (top)
     * @param addToBottom true if the books have to be added at the end of the list of items,
     * false if they are to be inserted at the specified position. Default = false.
     * @param preventDuplicates true if books with ids already present in the current items should not be added,
     * false if books with the same id are allowed to be in the items list
     */
    fun addBooks(books: List<ViewType>, insertAtPosition : Int = 0, addToBottom: Boolean = false,
                 preventDuplicates: Boolean = true) {
        if (!books.isEmpty()) {
            var booksToAdd = castItemsToBooks(books)
            if (preventDuplicates) {
                booksToAdd = booksToAdd?.filter { it.insertionId !in getCurrentBookIds() }
            }
            booksToAdd?.let {
                if (it.isEmpty()) return
                val insertPosition = if (addToBottom) itemCount - 1 else  insertAtPosition
                val insertItemCount = it.size
                items.addAll(insertPosition, it as List<ViewType>)
                notifyItemRangeInserted(insertPosition, insertItemCount)
                Log.d(TAG, "Added $insertItemCount books at position $insertPosition")
            }
        }
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
        Log.d(TAG, "Cleared items")
        addBooks(books)
    }

    /**
     * Update the current list of books with the new books.
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
            addBooks(toAdd)
        }
    }

    /**
     * Replace books in the current list with the given ones with the same id.
     *
     * @param newBooks the new version of the current books to update
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
     * Remove a list of books from the current items.
     *
     * @param books the list of books to remove
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
     * Get only the book items present in the adapter's list.
     */
    private fun getCurrentBookItems() : List<BookModel> = items.filterIsInstance<BookModel>()

    /**
     * Get the ids of the books present in the items list.
     */
    private fun getCurrentBookIds() = getCurrentBookItems().map { it.insertionId }

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