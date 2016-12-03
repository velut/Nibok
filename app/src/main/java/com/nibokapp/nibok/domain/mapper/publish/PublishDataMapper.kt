package com.nibokapp.nibok.domain.mapper.publish

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData
import com.nibokapp.nibok.extension.toRealmStringList
import com.nibokapp.nibok.extension.toStringList

/**
 * Implementation of a publish data mapper.
 */
class PublishDataMapper : PublishDataMapperInterface {

    override fun convertBookToBookData(book: Book?): BookData? = book?.let {
        BookData(it.id, it.title, it.authors.toStringList(), it.year, it.publisher, it.isbn)
    }

    override fun convertInsertionDataToInsertion(insertionData: InsertionData): Insertion {
        val book = with(insertionData.bookData) {
            Book(id, title, authors.toRealmStringList(), year, publisher, isbn)
        }
        return with(insertionData) {
            Insertion("", null, null, // Id, date and seller will be set by the server
                    book, bookPrice, bookConditionId.toString(), bookPictures.toRealmStringList())
        }
    }
}