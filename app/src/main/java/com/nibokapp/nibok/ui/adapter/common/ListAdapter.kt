package com.nibokapp.nibok.ui.adapter.common

/**
 * Interface of adapters dealing with lists of objects.
 */
interface ListAdapter<in T> {

    /**
     * Add items to a list.
     *
     * @param items the list of items to add
     */
    fun addItems(items: List<T>)

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

}
