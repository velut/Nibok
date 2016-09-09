package com.nibokapp.nibok.domain.model

/**
 * Schema representing essential information about a book.
 *
 * @param title the title of the book
 * @param authors the list of book's authors
 * @param year the year in which the book was printed
 * @param publisher the book's publisher
 * @param isbn the book's ISBN code
 */
data class BookInfoModel(
        val title: String,
        val authors: List<String>,
        val year: Int,
        val publisher: String,
        val isbn: String
)
