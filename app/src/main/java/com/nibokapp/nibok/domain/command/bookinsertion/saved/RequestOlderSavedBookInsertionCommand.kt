package com.nibokapp.nibok.domain.command.bookinsertion.saved

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instances that represent book insertions dated before the given
 * one.
 *
 * @param lastBookInsertion the last book insertion before the older ones
 */
class RequestOlderSavedBookInsertionCommand(
        val lastBookInsertion: BookInsertionModel,
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) :
        Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> =
            dataMapper.convertInsertionListToDomain(
                    bookRepository.getSavedBookInsertionListBeforeDate(
                            lastBookInsertion.insertionDate
                    )
            )
}