package com.nibokapp.nibok.domain.command.bookinsertion.published

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command

/**
 * Request a cached version of the BookInsertionModel instances
 * that make up insertions published by the local user.
 */
class RequestInsertionDeletion(
        val insertionId: String,
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<Boolean> {

    override fun execute(): Boolean {
        return bookRepository.deletePublishedInsertion(insertionId)
    }
}