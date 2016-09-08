package com.nibokapp.nibok.ui.presenter

import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.domain.model.DetailModel

/**
 * Presenter that retrieves full details about an insertion.
 */
class InsertionDetailPresenter {

    /**
     * Get detailed data about the insertion with the given id.
     *
     * @param insertionId the id of the insertion
     *
     * @return detail data about the insertion if the insertion was found, null otherwise
     */
    fun getInsertionDetails(insertionId: Long) : DetailModel? =
            BookManager.getInsertionDetails(insertionId)
}