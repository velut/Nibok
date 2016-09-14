package com.nibokapp.nibok.domain.mapper.bookinsertion

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.mapper.user.UserMapper
import com.nibokapp.nibok.domain.mapper.user.UserMapperInterface
import com.nibokapp.nibok.domain.model.BookInfoModel
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.domain.model.UserModel
import com.nibokapp.nibok.extension.toRealmStringList
import com.nibokapp.nibok.extension.toStringList

/**
 * Book insertion data mapper implementation.
 */
class BookInsertionDataMapper(
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository,
        val userMapper: UserMapperInterface = UserMapper()
) : BookInsertionDataMapperInterface {

    /*
     * BOOK INSERTION
     */

    override fun convertInsertionListToDomain(insertions: List<Insertion>) : List<BookInsertionModel> =
            insertions.map { convertInsertionToDomain(it) }.filterNotNull()

    override fun convertInsertionToDomain(insertion: Insertion?) : BookInsertionModel? {

        if (insertion == null || !insertion.isWellFormed()) {
            return null
        } else {
            return with(insertion) {
                BookInsertionModel(
                        insertionId = id,
                        seller = convertSellerToDomain(seller!!),
                        bookInfo = convertBookToDomain(book!!)!!,
                        bookPrice = bookPrice,
                        bookCondition = bookCondition,
                        bookPictureSources = bookImagesSources.toStringList(),
                        insertionDate = date!!,
                        savedByUser = bookRepository.isBookInsertionSaved(id)
                )
            }
        }
    }

    override fun convertInsertionListFromDomain(insertions: List<BookInsertionModel>):
            List<Insertion> = insertions.map { convertInsertionFromDomain(it) }

    override fun convertInsertionFromDomain(insertion: BookInsertionModel): Insertion = with(insertion) {
        Insertion(
                id = insertionId,
                date = insertionDate,
                seller = convertSellerFromDomain(seller),
                book = convertBookFromDomain(bookInfo),
                bookPrice = bookPrice,
                bookCondition = bookCondition,
                bookImagesSources = bookPictureSources.toRealmStringList()
        )
    }

    /*
     * BOOK INFO
     */

    override fun convertBookToDomain(book: Book?): BookInfoModel? {
        if (book == null) {
            return null
        } else {
            return with(book) {
                BookInfoModel(
                        title = title,
                        authors = authors.toStringList(),
                        year = year,
                        publisher = publisher,
                        isbn = isbn
                )
            }
        }
    }

    override fun convertBookFromDomain(bookInfo: BookInfoModel): Book = with(bookInfo) {
        Book(
                title = title,
                authors = authors.toRealmStringList(),
                year = year,
                publisher = publisher,
                isbn = isbn
        )
    }

    /*
     * SELLER
     */

    private fun convertSellerToDomain(seller: ExternalUser): UserModel =
            userMapper.convertUserToDomain(seller)

    private fun convertSellerFromDomain(seller: UserModel): ExternalUser =
            userMapper.convertUserFromDomain(seller)

    /*
     * EXTENSIONS
     */

    private fun Insertion.isWellFormed(): Boolean = with(this) {
            seller != null && date != null && book != null
    }
}
