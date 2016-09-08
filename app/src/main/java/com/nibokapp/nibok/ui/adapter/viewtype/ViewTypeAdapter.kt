package com.nibokapp.nibok.ui.adapter.viewtype

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import com.nibokapp.nibok.ui.adapter.viewtype.common.ListAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import com.nibokapp.nibok.ui.adapter.viewtype.delegate.BookDelegateAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.delegate.LoadingDelegateAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.delegate.MessageDelegateAdapter

/**
 * The adapter responsible for managing and displaying ViewType items.
 *
 * This adapter delegates the managing of subclasses of ViewType items to its delegate adapters
 * based on a correspondence (view type -> delegate adapter).
 *
 * @param itemClickListener function to be called if an item is clicked. Optional
 */
class ViewTypeAdapter(itemClickListener: ItemClickListener)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListAdapter<ViewType> {

    companion object {
        private val TAG = ViewTypeAdapter::class.java.simpleName
    }

    /**
     * Interface for objects that want to listen to clicks on a view type item.
     */
    interface ItemClickListener {

        /**
         * Listen to a click of a button of the item.
         *
         * @param itemId the id of the item that was clicked
         */
        fun onButtonClick(itemId: Long, itemType: Int)

        /**
         * Listen to a click on the item itself.
         *
         * @param itemId the id of the item that was clicked
         */
        fun onItemClick(itemId: Long, itemType: Int)
    }

    // The loading item object
    private val loadingItem = object : ViewType {
        override fun getItemId(): Long = 0L

        override fun getViewType(): Int = ViewTypes.LOADING
    }

    // Items to be displayed
    private val items = mutableListOf<ViewType>(loadingItem)

    // List of removed items
    private val removedItems = mutableListOf<ViewType>()

    // Adapter instances corresponding to view types
    private val delegateAdaptersMap = mapOf(
            ViewTypes.LOADING to LoadingDelegateAdapter(),
            ViewTypes.BOOK to BookDelegateAdapter(itemClickListener),
            ViewTypes.MESSAGE to MessageDelegateAdapter(itemClickListener)
    )

    // The currently supported view types
    private val supportedViewTypes = delegateAdaptersMap.keys

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

    override fun addItems(items: List<ViewType>, insertPosition: Int,
                          insertAtBottom: Boolean, excludeDuplicates: Boolean) =
            addViewTypeItems(items, insertPosition, insertAtBottom, excludeDuplicates)

    override fun clearAndAddItems(items: List<ViewType>) = clearAndAddViewTypeItems(items)

    override fun updateItems(items: List<ViewType>) = updateViewTypeItems(items)

    override fun removeItems(items: List<ViewType>) = removeViewTypeItems(items)

    override fun removeItem(item: ViewType) = removeViewTypeItem(item)

    override fun removeItemById(itemId: Long, itemType: Int): Int {
        var result = -1
        val itemToRemove = getCurrentItemsForViewType(itemType).find { it.getItemId() == itemId }
        itemToRemove?.let {
            removedItems.add(it)
            result = removeItem(it)
        }
        return result
    }

    override fun restoreItemById(itemId: Long, itemType: Int, position: Int) {
        val itemToRestore = removedItems.find { it.getItemId() == itemId
                                                && it.getViewType() == itemType }
        itemToRestore?.let {
            addViewTypeItems(listOf(it), insertPosition = position)
            removedItems.remove(it)
        }
    }

    /**
     * Add given items to the list of items to display.
     *
     * @param items the list of items to add
     * @param insertPosition the desired position at which items are inserted. Default = 0 (top)
     * @param insertAtBottom true if the items have to be added at the end of the list of items,
     * false if they are to be inserted at the specified position. Default = false.
     * @param excludeDuplicates true if items with ids already present in the current items
     * with the same view type should not be added,
     * false if items with the same id and the same view type are allowed to be in the items list
     */
    private fun addViewTypeItems(items: List<ViewType>, insertPosition: Int = 0,
                                 insertAtBottom: Boolean = false, excludeDuplicates: Boolean = true) {

        if (items.isEmpty()) return

        var itemsToAdd: List<ViewType> = items

        if (excludeDuplicates) {
            itemsToAdd = emptyList()
            supportedViewTypes.forEach {
                val currentType = it
                val candidateItems = items.filter { it.getViewType() == currentType }
                if (candidateItems.isNotEmpty()) {
                    val currentIds = getCurrentIdsForViewType(currentType)
                    itemsToAdd += candidateItems.filter { it.getItemId() !in currentIds }
                }
            }
            Log.d(TAG, "After duplicates removal size is: ${itemsToAdd.size}")
        }

        if (itemsToAdd.isNotEmpty()) {
            var insertPos = if (insertAtBottom) itemCount - 1 else insertPosition
            if (insertPos < 0 || insertPos >= itemCount) insertPos = 0
            val insertItemCount = itemsToAdd.size
            this.items.addAll(insertPos, itemsToAdd)
            notifyItemRangeInserted(insertPos, insertItemCount)
            Log.d(TAG, "Added $insertItemCount items at position $insertPos")
        }
    }

    /**
     * Clear the items list and add the given items.
     *
     * @param items the items to add to the items list
     */
    private fun clearAndAddViewTypeItems(items: List<ViewType>) {
        val oldItemCount = itemCount
        this.items.clear()
        notifyItemRangeRemoved(0, oldItemCount)
        Log.d(TAG, "Cleared items")
        addViewTypeItems(items)
    }

    /**
     * Update the current list of items with the given new items.
     *
     * An item already present in the current list is updated with its new version,
     * a new item not already present in the list is inserted into it.
     *
     * @param items the list of items to add or update in the current list of displayed items
     */
    private fun updateViewTypeItems(items: List<ViewType>) {
        supportedViewTypes.forEach {
            val currentType = it
            val newItems = items.filter { it.getViewType() == currentType }
            if (newItems.isNotEmpty()) {
                val currentIds = getCurrentIdsForViewType(currentType)
                val (toReplace, toAdd) = newItems.partition { it.getItemId() in currentIds }
                Log.d(TAG, "Items of type: ${ViewTypes.getTypeName(currentType)}\n" +
                        "To replace: ${toReplace.size}; to add: ${toAdd.size}")
                replaceViewTypeItems(toReplace, currentType)
                addViewTypeItems(toAdd)
            }
        }
    }

    /**
     * Replace current items with a certain view type
     * with the given new items with the same view type.
     *
     * @param items the new version of items already present in the current items list
     * @param itemsType the view type being considered
     */
    private fun replaceViewTypeItems(items: List<ViewType>, itemsType: Int) =
            items.forEach { replaceViewTypeItem(it, itemsType) }

    /**
     * Replace an old item with a certain view type with its new version.
     *
     * @param item the new version of the item
     * @param itemType the view type of the item
     */
    private fun replaceViewTypeItem(item: ViewType, itemType: Int) {
        val candidateItems = getCurrentItemsForViewType(itemType)
        val itemToReplace = candidateItems.find { it.getItemId() == item.getItemId() }
        itemToReplace?.let {
            Log.d(TAG, "Replacing ${ViewTypes.getTypeName(itemType)} with id: ${it.getItemId()}")
            val itemToReplaceIndex = this.items.indexOf(it)
            this.items[itemToReplaceIndex] = item
            notifyItemChanged(itemToReplaceIndex)
        }
    }

    /**
     * Remove the given items from the current list of items.
     *
     * @param items the list of items to remove
     */
    private fun removeViewTypeItems(items: List<ViewType>) {
        Log.d(TAG, "To remove: ${items.size}")
        items.forEach { removeViewTypeItem(it) }
    }

    /**
     * Remove the given item if it's present in the current list of items.
     *
     * @param item the item to remove
     *
     * @return the position that the item had in the list
     * or -1 if the item wasn't present in the list
     */
    private fun removeViewTypeItem(item: ViewType): Int {
        val itemIndex = this.items.indexOf(item)
        if (itemIndex != -1) {
            this.items.removeAt(itemIndex)
            notifyItemRemoved(itemIndex)
            Log.d(TAG, "Removed ${ViewTypes.getTypeName(item.getViewType())} with id: ${item.getItemId()}")
        }
        return itemIndex
    }

    /**
     * Get the items in the current list that match the given view type.
     *
     * @param viewType the desired view type
     *
     * @return the list of currently managed items with the given view type
     */
    private fun getCurrentItemsForViewType(viewType: Int) =
            items.filter { it.getViewType() == viewType }

    /**
     * Get the ids of the items in the current list that match the given view type.
     *
     * @param viewType the desired view type
     *
     * @return the list of ids of the currently managed items with the given view type
     */
    private fun getCurrentIdsForViewType(viewType: Int) =
            getCurrentItemsForViewType(viewType).map { it.getItemId() }

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