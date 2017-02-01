package com.nibokapp.nibok.ui.presenter.main

/**
 * Interface for presenters used in the main activity.
 */
interface MainActivityPresenter<T> {

    /**
     * Get the list of items to present in the main view.
     *
     * @return a list of items
     */
    fun getData(): List<T>

    /**
     * Get a cached version of the items to present in the main view.
     *
     * @return a list of items
     */
    fun getCachedData(): List<T>

    /**
     * Get the list of items older than the given item.
     *
     * @return the list of items older than the given item
     */
    fun getDataOlderThanItem(item: T): List<T>

    /**
     * Get the list of items matching the given query.
     *
     * @param query the string representing the query
     *
     * @return the list of T items matching the given query
     *         or an empty list if no match was found
     */
    fun getQueryData(query: String): List<T>
}