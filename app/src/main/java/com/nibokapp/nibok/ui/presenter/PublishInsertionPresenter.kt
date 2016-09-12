package com.nibokapp.nibok.ui.presenter

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.publishing.RequestBookInfoByIsbnCommand
import com.nibokapp.nibok.domain.model.BookInfoModel

/**
 * Presenter for the insertion's publishing view.
 */
class PublishInsertionPresenter {

    companion object {
        private val TAG = PublishInsertionPresenter::class.java.simpleName
    }

    /**
     * Get book data given a book ISBN code.
     *
     * @param isbn the isbn code of a book
     *
     * @return data about the book with the given isbn code or null if not data was found
     */
    fun getBookDataByISBN(isbn: String) : BookInfoModel? {
        Log.d(TAG, "Requesting book data for ISBN: $isbn")
        return RequestBookInfoByIsbnCommand(isbn).execute()
    }

    /**
     * Publish the given insertion.
     *
     * @param insertion the insertion to publish TODO
     *
     * @return true if the insertion was successfully published, false otherwise
     */
    fun publishInsertion() : Boolean {
        // TODO
        return false
    }
}