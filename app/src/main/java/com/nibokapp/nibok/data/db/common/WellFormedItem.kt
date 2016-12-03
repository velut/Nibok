package com.nibokapp.nibok.data.db.common

/**
 * WellFormedItem.
 * This interface provides a method to check if an item used by the local database is well formed,
 * that is it contains valid and meaningful data to use in the application.
 */
interface WellFormedItem {

    /**
     * Check if an item is well formed.
     *
     * @return true if the item is well formed, false otherwise
     */
    fun isWellFormed(): Boolean
}