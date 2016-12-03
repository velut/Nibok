package com.nibokapp.nibok.domain.mapper.publish

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.extension.toStringList

/**
 * Implementation of a publish data mapper.
 */
class PublishDataMapper : PublishDataMapperInterface {

    override fun convertBookToBookData(book: Book?): BookData? = book?.let {
        BookData(it.id, it.title, it.authors.toStringList(), it.year, it.publisher, it.isbn)
    }
}