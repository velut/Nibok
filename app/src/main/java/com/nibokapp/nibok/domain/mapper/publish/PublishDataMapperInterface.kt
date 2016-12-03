package com.nibokapp.nibok.domain.mapper.publish

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData

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

    /**
     * Convert [InsertionData] to [Insertion].
     *
     * @param insertionData the insertion data to convert
     *
     * @return an [Insertion] built form the given [InsertionData]
     */
    fun convertInsertionDataToInsertion(insertionData: InsertionData): Insertion


}