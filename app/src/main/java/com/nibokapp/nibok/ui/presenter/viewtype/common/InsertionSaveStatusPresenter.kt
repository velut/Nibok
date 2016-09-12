package com.nibokapp.nibok.ui.presenter.viewtype.common

import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface

/**
 * Interface for presenters that can check or change the save status of an insertion.
 */
interface InsertionSaveStatusPresenter {

    val insertionRepository: BookInsertionRepositoryInterface

    /**
     * Check if the insertion with the given id is saved or not.
     *
     * @param insertionId the id of the insertion
     *
     * @return true if the insertion is saved, false otherwise
     */
    fun isInsertionSaved(insertionId: Long) : Boolean =
            insertionRepository.isBookInsertionSaved(insertionId)

    /**
     * Toggle the save status of the insertion with the given id.
     * If the insertion was saved it is unsaved and vice versa.
     *
     * @param insertionId the id of the insertion
     *
     * @return true if the insertion was saved, false if it was unsaved
     */
    fun toggleInsertionSave(insertionId: Long) : Boolean =
            insertionRepository.toggleBookInsertionSaveStatus(insertionId)

}
