package com.nibokapp.nibok.domain.command.bookinsertion.publishing

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.publish.PublishDataMapper
import com.nibokapp.nibok.domain.model.publish.BookData

/**
 * Request information about a book given its ISBN code.
 *
 * @param isbn the ISBN code of the book
 *
 * @return [BookData] about the book if data was available, null otherwise
 */
class RequestBookInfoByIsbnCommand(
        val isbn: String,
        val dataMapper: PublishDataMapper = PublishDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<BookData?> {

    override fun execute(): BookData? =
            dataMapper.convertBookToBookData(
                bookRepository.getBookByISBN(isbn)
            )
}