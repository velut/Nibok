package com.nibokapp.nibok.ui.presenter

import com.nibokapp.nibok.domain.model.BookInfoModel

/**
 * Presenter for the insertion's publishing view.
 */
class PublishInsertionPresenter {

    /**
     * Get book data given a book ISBN code.
     *
     * @param isbnCode the isbn code of a book
     *
     * @return data about the book with the given isbn code or null if not data was found
     */
    fun getBookDataFromISBN(isbnCode: String) : BookInfoModel? {
        // TODO
        return null
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