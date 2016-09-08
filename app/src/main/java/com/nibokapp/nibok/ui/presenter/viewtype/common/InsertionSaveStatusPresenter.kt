package com.nibokapp.nibok.ui.presenter.viewtype.common

import com.nibokapp.nibok.data.repository.UserManager

/**
 * Interface for presenters that can check or change the save status of an insertion.
 */
interface InsertionSaveStatusPresenter {

    // TODO extract user manager

    /**
     * Check if the insertion with the given id is saved or not.
     *
     * @param insertionId the id of the insertion
     *
     * @return true if the insertion is saved, false otherwise
     */
    fun isInsertionSaved(insertionId: Long) : Boolean =
            UserManager.isInsertionSaved(insertionId)

    /**
     * Toggle the save status of the insertion with the given id.
     * If the insertion was saved it is unsaved and vice versa.
     *
     * @param insertionId the id of the insertion
     *
     * @return true if the insertion was saved, false if it was unsaved
     */
    fun toggleInsertionSave(insertionId: Long) : Boolean =
            UserManager.toggleSaveInsertion(insertionId)

}
