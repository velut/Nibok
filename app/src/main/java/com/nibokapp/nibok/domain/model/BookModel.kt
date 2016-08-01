package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ViewType

/**
 * Schema representing a book.
 *
 * The book should be represented by a book delegate adapter.
 *
 * @property title the title of the book
 * @property author the author of the book
 * @property year the year in which the book was published
 * @property quality the wear condition of the book
 * @property priceIntPart the integer part of the price
 * @property priceFracPart the fractional part of the price
 * @property thumbnail the thumbnail of the book
 * @property saved true if the book was saved by the user, false otherwise
 */
data class BookModel(
        val title: String,
        val author: String,
        val year: Int,
        val quality: String,
        val priceIntPart: Int,
        val priceFracPart: Int,
        val thumbnail: String,
        var saved: Boolean = false
) : ViewType {
    override fun getViewType(): Int = AdapterTypes.BOOK
}

