package com.nibokapp.nibok.ui.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log

/**
 * Interface for adapters that can be updated through DiffUtil.
 * Updatable adapters hold a list of items of type T.
 */
interface UpdatableAdapter<T> {

    /**
     * The list of items held by the adapter.
     */
    var items: List<T>

    /**
     * Update an adapter through DiffUtil and then dispatch the updates.
     *
     * @param oldItems the old list of items held by the adapter
     * @param newItems the new list of items held by the adapter
     * @param areItemsTheSame the function comparing two items (one old and one new) and checking if they are the same
     * @param getChangePayload the function calculating the change payload between the old version of an item and the new one
     */
    fun RecyclerView.Adapter<*>.update(oldItems: List<T>,
                                       newItems: List<T>,
                                       areItemsTheSame: (T, T) -> Boolean,
                                       getChangePayload: (T, T) -> Any?) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return getChangePayload(oldItem, newItem)
            }
        })

        Log.d("UpdatableAdapter", "Dispatching updates")
        diff.dispatchUpdatesTo(this)
    }
}