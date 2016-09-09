package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import java.util.*

/**
 * Schema representing information about a book's insertion.
 *
 * @param insertionId the id of the insertion in which the book appears
 * @param seller the user who published the insertion
 * @param bookInfo editorial data about the book
 * @param bookPrice the price of the book
 * @param bookCondition the wear condition of the book
 * @param bookPictureSources the list of sources for pictures of the book
 * associated to the insertion. The first picture is the thumbnail
 * @param savedByUser true if the insertion is saved by the user, false if it is not
 * @param insertionDate the date in which the insertion was published
 */
data class BookInsertionModel(
        val insertionId: Long,
        val seller: UserModel,
        val bookInfo: BookInfoModel,
        val bookPrice: Float,
        val bookCondition: String,
        val bookPictureSources: List<String>,
        val savedByUser: Boolean,
        val insertionDate: Date
) : ViewType {

    override fun getItemId(): Long = insertionId

    override fun getViewType(): Int = ViewTypes.BOOK_INSERTION
}

