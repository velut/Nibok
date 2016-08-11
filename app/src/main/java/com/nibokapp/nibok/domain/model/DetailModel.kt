package com.nibokapp.nibok.domain.model

import java.util.*

/**
 * Schema representing detail information about a book to display in a detail view.
 *
 * @property insertionId the id of the insertion in which the book appears
 * @property bookPrice the price of the book
 * @property bookCondition the wear condition of the book
 * @property sellerName the name of the seller
 * @property insertionDate the publishing date of the insertion
 * @property bookTitle the title of the book
 * @property bookAuthors the author of the book
 * @property bookYear the year in which the book was published
 * @property bookPublisher the publisher of the book
 * @property bookISBN the ISBN of the book
 */
data class DetailModel(
        val insertionId: Long,
        val bookPrice: Float,
        val bookCondition: String,
        val sellerName: String,
        val insertionDate: Date,
        val bookTitle: String,
        val bookAuthors: List<String>,
        val bookYear: Int,
        val bookPublisher: String,
        val bookISBN: String,
        val bookImgSources: List<String> = listOf("")
)

