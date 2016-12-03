package com.nibokapp.nibok.ui.presenter

import android.util.Log
import com.nibokapp.nibok.domain.command.bookinsertion.publishing.PublishBookInsertionCommand
import com.nibokapp.nibok.domain.command.bookinsertion.publishing.RequestBookInfoByIsbnCommand
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData

/**
 * Presenter used by the insertion publishing view.
 */
class PublishInsertionPresenter {

    companion object {
        private val TAG = PublishInsertionPresenter::class.java.simpleName
    }

    /**
     * Get book data given the ISBN code of a book.
     *
     * @param isbn the isbn code of a book
     *
     * @return [BookData] about the book with the given isbn code or null if no data was found
     */
    fun getBookDataByIsbn(isbn: String): BookData? {
        Log.d(TAG, "Requesting book data for ISBN: $isbn")
        return RequestBookInfoByIsbnCommand(isbn).execute()
    }

    /**
     * Publish the insertion with the given data.
     *
     * @param insertionData the data about the insertion to publish
     *
     * @return true if the insertion was successfully published, false otherwise
     */
    fun publishInsertion(insertionData: InsertionData): Boolean {
        Log.d(TAG, "Publishing book insertion for: $insertionData")
        return PublishBookInsertionCommand(insertionData).execute()
    }
}