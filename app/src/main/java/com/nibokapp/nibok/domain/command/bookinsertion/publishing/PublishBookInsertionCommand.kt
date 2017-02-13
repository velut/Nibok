package com.nibokapp.nibok.domain.command.bookinsertion.publishing

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.publish.PublishDataMapper
import com.nibokapp.nibok.domain.model.publish.InsertionData

/**
 * Publish a book insertion given the associated [InsertionData].
 *
 * @param insertionData the data about the insertion to be published
 * @param dataMapper the data mapper used to convert data for the repository
 * @param bookRepository the book repository
 *
 * @return true if the insertion was published successfully, false otherwise
 */
class PublishBookInsertionCommand(
        val insertionData: InsertionData,
        val dataMapper: PublishDataMapper = PublishDataMapper(),
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<Boolean> {

    override fun execute(): Boolean {
        return bookRepository.publishInsertion(
                dataMapper.convertInsertionDataToInsertion(insertionData)
        ) != null
    }
}