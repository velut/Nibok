package com.nibokapp.nibok.domain.command.bookinsertion

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instance corresponding to the given book insertion id.
 *
 * @param insertionId the id of the book insertion
 *
 * @return a BookInsertionModel representing the insertion with the given id
 * if such insertion exists, null if no such insertion exists
 */
class RequestBookInsertionByIdCommand(
        val insertionId: Long,
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<BookInsertionModel?> {

    override fun execute(): BookInsertionModel? =
            dataMapper.convertInsertionToDomain(
                    bookRepository.getBookInsertionById(insertionId)
            )
}