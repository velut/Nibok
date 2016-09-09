package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes

/**
 * Schema representing essential information about a book to display in a card.
 *
 * The book card model is used in the book delegate adapter to bind data into the card view.
 *
 * @param insertionId the id of the insertion in which the book appears
 * @param title the title of the book
 * @param author the author of the book
 * @param year the year in which the book was published
 * @param quality the wear condition of the book
 * @param price the price of the book
 * @param thumbnail the thumbnail of the book
 * @param saved true if the book was saved by the user, false otherwise
 */
data class BookModel(
        val insertionId: Long,
        val title: String,
        val author: String,
        val year: Int,
        val quality: String,
        val price: Float,
        val thumbnail: String,
        var saved: Boolean = false
) : ViewType {

    override fun getItemId(): Long = insertionId

    override fun getViewType(): Int = ViewTypes.BOOK_INSERTION
}

