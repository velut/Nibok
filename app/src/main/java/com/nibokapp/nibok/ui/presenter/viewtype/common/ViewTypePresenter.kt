package com.nibokapp.nibok.ui.presenter.viewtype.common

import com.nibokapp.nibok.ui.adapter.common.ViewType

/**
 * Interface for ViewType presenters, that is objects whose duty is to retrieve ViewType data
 * to be displayed in ViewType fragments.
 */
interface ViewTypePresenter {

    /**
     * Get the list of ViewType items to present in a view.
     *
     * @return the current list of items making up the data to represent in a view
     */
    fun getData() : List<ViewType>

    /**
     * Get a cached version of the ViewType items to present in a view.
     *
     * @return a cached list of items making up the data to represent in a view
     */
    fun getCachedData() : List<ViewType>

    /**
     * Get the difference between the new available data and the given old data.
     *
     * @param oldData the old version of the data used as the base to calculate the difference
     *
     * @return a triple of ViewType lists, the first element represents the items that were added
     * with respect to the old data, the second element represents the items that were removed
     * with respect to the old data and the third element represents the new data used to calculate
     * the diff with respect to the old data
     */
    fun getDiffData(oldData: List<ViewType>) :
            Triple<List<ViewType>, List<ViewType>, List<ViewType>> {

        val newData = getData()

        if (oldData == newData) return Triple(emptyList(), emptyList(), newData)

        val additions = newData.filter { it !in oldData }
        val deletions = oldData.filter { it !in newData }

        return Triple(additions, deletions, newData)
    }

    /**
     * Get the list of ViewType items matching the given query.
     *
     * @param query the string representing the query
     *
     * @return the list of ViewType items matching the given query
     * or an empty list if no match was possible
     */
    fun getQueryData(query: String) : List<ViewType>
}
