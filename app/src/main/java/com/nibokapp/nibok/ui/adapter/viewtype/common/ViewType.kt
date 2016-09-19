package com.nibokapp.nibok.ui.adapter.viewtype.common

/**
 * Interface for items managed by the ViewTypeAdapter.
 *
 * The view type helps the main adapter when choosing the appropriate delegate adapters.
 */

interface ViewType {

    /**
     * Get the id of the item.
     *
     * @return the id of the item
     */
    fun getItemId(): String

    /**
     * Get the view type of the item.
     *
     * @return the view type of the item
     */
    fun getViewType() : Int
}