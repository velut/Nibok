package com.nibokapp.nibok.ui.presenter

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.RequestBookInsertionByIdCommand
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Presenter that retrieves full details about an insertion.
 */
class InsertionDetailPresenter {

    companion object {
        private val TAG = InsertionDetailPresenter::class.java.simpleName
    }

    /**
     * Get detailed data about the insertion with the given id.
     *
     * @param insertionId the id of the insertion
     *
     * @return detail data about the insertion if the insertion was found, null otherwise
     */
    fun getInsertionDetails(insertionId: Long) : BookInsertionModel? {
        Log.d(TAG, "Getting book insertion $insertionId details")
        return RequestBookInsertionByIdCommand(insertionId).execute()
    }
}