package com.nibokapp.nibok.domain.mapper.publish

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.domain.model.publish.BookData

/**
 * Interface for mappers that operate on publishing data.
 */
interface PublishDataMapperInterface {

    /**
     * Convert a [Book] into [BookData].
     *
     * @param book the book to convert
     *
     * @return the [BookData] associated to the book if the book was not null, null otherwise
     */
    fun convertBookToBookData(book: Book?): BookData?


}