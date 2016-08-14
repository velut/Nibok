package com.nibokapp.nibok.data.mapper

import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.common.RealmString
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.domain.model.DetailModel
import com.nibokapp.nibok.extension.toStringList
import com.nibokapp.nibok.ui.App
import io.realm.RealmList

class DbDataMapper {

    companion object {
        val AUTHOR_PLACEHOLDER = R.string.placeholder_no_info
        val THUMB_PLACEHOLDER = ""
    }

    /**
     * Build a DetailModel from insertion data.
     *
     * @param insertion the considered insertion
     *
     * @return a DetailModel instance containing detail data about the insertion
     */
    fun convertInsertionToDetailDomain(insertion: Insertion) : DetailModel = with(insertion) {
        DetailModel(
                id,
                bookPrice,
                bookCondition,
                seller!!.name,
                date!!,
                book!!.title,
                book!!.authors!!.toStringList(),
                book!!.year,
                book!!.publisher,
                book!!.isbn
        )
    }

    /**
     * Build a list of BookModel from the list of results associated to a query on insertions.
     *
     * @param insertions the list of insertions
     *
     * @return a list of BookModel instances
     */
    fun convertInsertionListToBookDomain(insertions: List<Insertion>) : List<BookModel> =
            insertions.map { convertInsertionToBookDomain(it) }.filterNotNull()

    /**
     * Build a BookModel instance out of the data available in an insertion.
     *
     * @param insertion the considered insertion
     *
     * @return a BookModel instance if book data was available, null otherwise
     */
    fun convertInsertionToBookDomain(insertion: Insertion) : BookModel? = with(insertion) {
        book?.let {
            BookModel(
                    id,
                    it.title,
                    getAuthors(it.authors),
                    it.year,
                    bookCondition,
                    bookPrice,
                    getThumbnail(bookImagesSources),
                    UserManager.isInsertionSaved(id)
            )
        }
    }

    /**
     * Helper function.
     * Join the authors names in a single string with newlines separating the names.
     *
     * @param authorsList the list of authors retrieved from the db
     *
     * @return a single string with the authors names separated by a newline
     *  or a placeholder string if authors were not found
     */
    private fun getAuthors(authorsList: RealmList<RealmString>?) : String =
            authorsList?.toStringList()?.joinToString("\n")
                    ?: App.instance.getString(AUTHOR_PLACEHOLDER)

    /**
     * Helper function.
     * Get the thumbnail for the book.
     * The thumbnail is the first image in the list of images associated to the insertion.
     *
     * @param imgList the list of images urls associated to the insertion and retrieved from the db
     *
     * @return the url of the first image in the list or a placeholder if no image is available
     */
    private fun getThumbnail(imgList: RealmList<RealmString>?) : String =
            imgList?.get(0)?.value ?: THUMB_PLACEHOLDER
}
