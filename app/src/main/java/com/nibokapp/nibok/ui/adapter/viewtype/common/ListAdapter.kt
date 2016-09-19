package com.nibokapp.nibok.ui.adapter.viewtype.common

/**
 * Interface of adapters dealing with lists of objects.
 */
interface ListAdapter<T> {

    /**
     * Add items to a list.
     *
     * @param items the list of items to add
     * @param insertPosition the position at which to insert the items. Default is 0 which means at the top
     * @param insertAtBottom true if the items should be inserted at the bottom, false otherwise. Default is false
     * @param excludeDuplicates true if from the given items should be excluded items already present,
     * false if duplicates are allowed. Default is true which exclude duplicates
     *
     * @return the number of items that were added
     */
    fun addItems(items: List<T>, insertPosition: Int = 0,
                 insertAtBottom: Boolean = false, excludeDuplicates: Boolean = true) : Int

    /**
     * Clear the list and add the given items.
     *
     * @param items the list of items to add
     */
    fun clearAndAddItems(items: List<T>)

    /**
     * Update the list with the given items.
     *
     * @param items the new or updated items to put in the list
     */
    fun updateItems(items: List<T>)

    /**
     * Remove from the list the given items.
     *
     * @param items the items to be removed from the list
     */
    fun removeItems(items: List<T>)

    /**
     * Remove the given item from the list and return its index.
     *
     * @param item the item to remove
     *
     * @return the index of the item if the item was remove successfully
     * or -1 if the item couldn't be found
     */
    fun removeItem(item: T) : Int

    /**
     * Remove the item with the given id from the list and return its index.
     *
     * @param itemId the id of the item to remove
     * @param itemType the view type of the item
     *
     * @return the index of the item if the item was remove successfully
     * or -1 if the item couldn't be found
     */
    fun removeItemById(itemId: String, itemType: Int) : Int

    /**
     * Restore an item into the current list of items given its id.
     *
     * @param itemId the id of the item to restore
     * @param itemType the view type of the item
     * @param position the position in the items' list in which to restore the item
     */
    fun restoreItemById(itemId: String, itemType: Int, position: Int)

    /**
     * Get the items currently managed by the adapter.
     *
     * @return the items currently managed by the adapter
     */
    fun getItems() : List<T>

}
