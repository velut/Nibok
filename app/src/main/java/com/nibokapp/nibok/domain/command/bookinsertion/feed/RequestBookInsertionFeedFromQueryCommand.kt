package com.nibokapp.nibok.domain.command.bookinsertion.feed

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the latest BookInsertionModel instances that make up the feed of books insertions
 * matching the given query.
 */
class RequestBookInsertionFeedFromQueryCommand(
        val query: String,
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> =
            dataMapper.convertInsertionListToDomain(
                    bookRepository.getFeedBookInsertionListFromQuery(query)
            )
}