package com.nibokapp.nibok.domain.model

import java.util.*

/**
 * Schema representing detail information about a book to display in a detail view.
 *
 * @param insertionId the id of the insertion in which the book appears
 * @param bookPrice the price of the book
 * @param bookCondition the wear condition of the book
 * @param sellerName the name of the seller
 * @param insertionDate the publishing date of the insertion
 * @param bookTitle the title of the book
 * @param bookAuthors the author of the book
 * @param bookYear the year in which the book was published
 * @param bookPublisher the publisher of the book
 * @param bookISBN the ISBN of the book
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

