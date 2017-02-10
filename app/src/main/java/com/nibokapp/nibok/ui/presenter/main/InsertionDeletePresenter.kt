package com.nibokapp.nibok.ui.presenter.main

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.published.RequestInsertionDeletion

/**
 * Interface for presenters able to remove insertions.
 */
interface InsertionDeletePresenter {

    /**
     * Delete the insertion with the given id.
     *
     * @param insertionId the id of the insertion to delete
     *
     * @return true if the insertion was deleted, false otherwise
     */
    fun deleteInsertion(insertionId: String): Boolean {
        Log.d("InsDeletePresenter", "Deleting insertion: $insertionId")
        return RequestInsertionDeletion(insertionId).execute()
    }
}