package com.nibokapp.nibok.domain.mapper

import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.BookInfoModel
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.domain.model.UserModel
import com.nibokapp.nibok.extension.toStringList

/**
 * Book insertion data mapper implementation.
 */
class BookInsertionDataMapper : BookInsertionDataMapperInterface {

    override fun convertInsertionListToDomain(insertions: List<Insertion>) : List<BookInsertionModel> =
            insertions.map { convertInsertionToDomain(it) }.filterNotNull()

    override fun convertInsertionToDomain(insertion: Insertion) : BookInsertionModel? = with(insertion) {
        if (isWellFormed()) {
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
        } else {
            null
        }
    }

    private fun Insertion.isWellFormed(): Boolean = with(this) {
            seller != null
            date != null
            book != null
    }
}
