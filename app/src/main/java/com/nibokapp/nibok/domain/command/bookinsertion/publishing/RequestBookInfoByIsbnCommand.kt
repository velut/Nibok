package com.nibokapp.nibok.domain.command.bookinsertion.publishing

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInfoModel

/**
 * Request infromations about a book given its ISBN code.
 *
 * @param isbn the ISBN code of the book
 *
 * @return a BookInfoModel if data was available, null otherwise
 */
class RequestBookInfoByIsbnCommand(
        val isbn: String,
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<BookInfoModel?> {

    override fun execute(): BookInfoModel? =
            dataMapper.convertBookToDomain(
                    bookRepository.getBookFromISBN(isbn)
            )
}