package com.nibokapp.nibok.domain.command.bookinsertion.published

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request a cached version of the BookInsertionModel instances
 * that make up insertions published by the local user.
 */
class RequestCachedPublishedBookInsertionCommand(
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> =
            dataMapper.convertInsertionListToDomain(
                    bookRepository.getPublishedInsertionList(cached = true)
            )
}