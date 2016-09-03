package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.common.ViewTypes
import com.nibokapp.nibok.ui.adapter.common.ViewType

/**
 * Schema representing essential information about a book to display in a card.
 *
 * The book card model is used in the book delegate adapter to bind data into the card view.
 *
 * @property insertionId the id of the insertion in which the book appears
 * @property title the title of the book
 * @property author the author of the book
 * @property year the year in which the book was published
 * @property quality the wear condition of the book
 * @property price the price of the book
 * @property thumbnail the thumbnail of the book
 * @property saved true if the book was saved by the user, false otherwise
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

    override fun getViewType(): Int = ViewTypes.BOOK
}

