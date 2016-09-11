package com.nibokapp.nibok.domain.mapper

import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.BookInfoModel
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.domain.model.UserModel
import com.nibokapp.nibok.extension.toStringList

/**
 * Mapper that uses database objects to build domain models.
 */
class BookInsertionDataMapper {

    /**
     * Build a list of BookInsertionModel given a list of insertions from the DB.
     * Insertions with invalid data are dropped from the returned list.
     *
     * @param insertions the list of insertions obtained from the DB
     *
     * @return a list of BookInsertionModel instances
     */
    fun convertInsertionListToDomain(insertions: List<Insertion>) : List<BookInsertionModel> =
            insertions.map { convertInsertionToDomain(it) }.filterNotNull()

    /**
     * Build a BookInsertionModel from a DB insertion.
     *
     * @param insertion the considered insertion
     *
     * @return a BookInsertionModel instance containing detail data about the book's insertion
     * if insertion data is complete, null otherwise
     */
    fun convertInsertionToDomain(insertion: Insertion) : BookInsertionModel? = with(insertion) {

        // Discard malformed insertions and return null
        if (seller == null || book == null || date == null) return@with null

        BookInsertionModel(
                insertionId = id,
                seller = with(seller!!) {
                    UserModel(id, name, avatar)
                },
                bookInfo = with(book!!) {
                    BookInfoModel(title, authors.toStringList(), year, publisher, isbn)
                },
                bookPrice = bookPrice,
                bookCondition = bookCondition,
                bookPictureSources = bookImagesSources.toStringList(),
                insertionDate = date!!,
                savedByUser = UserManager.isInsertionSaved(id) // TODO maybe a better solution?
        )
    }

}
