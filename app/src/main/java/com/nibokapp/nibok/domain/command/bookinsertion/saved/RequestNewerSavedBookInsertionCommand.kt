package com.nibokapp.nibok.domain.command.bookinsertion.saved

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapper
import com.nibokapp.nibok.domain.mapper.bookinsertion.BookInsertionDataMapperInterface
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instances that represent book insertions dated after the given
 * one.
 *
 * @param firstBookInsertion the first book insertion before the newer ones
 */
class RequestNewerSavedBookInsertionCommand(
        val firstBookInsertion: BookInsertionModel,
        val dataMapper: BookInsertionDataMapperInterface = BookInsertionDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) :
        Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> =
            dataMapper.convertInsertionListToDomain(
                    bookRepository.getSavedInsertionLisAfterDate(
                            firstBookInsertion.insertionDate
                    )
            )
}