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

/**
 * The adapter responsible for the overall book view.
 *
 * It delegates the managing of the items in the view to the respective adapters based on the
 * view type and adapter type.
 */
class BookAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListAdapter<ViewType> {

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

    override fun updateItems(items: List<ViewType>) = updateBooks(items)

    override fun removeItems(items: List<ViewType>) = removeBooks(items)

    /**
     * Add books to the list of items to display.
     *
     * @param books the list of books to add
     * @param addToTop true if the books have to be added at the start of the list of items,
     * false if they are to be inserted at the end of the list (before the loading item). Default true.
     */
    fun addBooks(books: List<ViewType>, addToTop: Boolean = true) {
        if (!books.isEmpty()) {
            val insertPosition = if (addToTop) 0 else itemCount - 1 // at top or at loading item position
            val insertItemCount = books.size
            items.addAll(insertPosition, books)
            notifyItemRangeInserted(insertPosition, insertItemCount)
            Log.d(TAG, "Added $insertItemCount books at position $insertPosition")
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
        val currentBooksIds = getBookItems().map { it.insertionId }
        updatedBooks?.let {
            val (toReplace, toAdd) = it.partition { it.insertionId in currentBooksIds }
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
        val currentBooks = getBookItems()
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
     */
    fun removeBook(book: BookModel) {
        val currentBooks = getBookItems()
        val bookToRemove = currentBooks.find { it.insertionId == book.insertionId }
        bookToRemove?.let {
            Log.d(TAG, "Removing book with id: ${bookToRemove.insertionId}")
            val bookIndex = items.indexOf(bookToRemove)
            items.removeAt(bookIndex)
            notifyItemRemoved(bookIndex)
        }
    }


    /**
     * Get only the book items present in the adapter's list.
     */
    private fun getBookItems() : List<BookModel> = items.filterIsInstance<BookModel>()

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