package com.nibokapp.nibok.domain.mapper

import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.model.BookInfoModel
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.domain.model.UserModel
import com.nibokapp.nibok.extension.toStringList

/**
 * Book insertion data mapper implementation.
 */
class BookInsertionDataMapper(
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : BookInsertionDataMapperInterface {

    override fun convertInsertionListToDomain(insertions: List<Insertion>) : List<BookInsertionModel> =
            insertions.map { convertInsertionToDomain(it) }.filterNotNull()

    override fun convertInsertionToDomain(insertion: Insertion?) : BookInsertionModel? {

        if (insertion == null || !insertion.isWellFormed()) {
            return null
        } else {
            return with(insertion) {
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
                        savedByUser = bookRepository.isBookInsertionSaved(id)
                )
            }
        }
    }

    /*
     * EXTENSIONS
     */

    private fun Insertion.isWellFormed(): Boolean = with(this) {
            seller != null && date != null && book != null
    }
}
