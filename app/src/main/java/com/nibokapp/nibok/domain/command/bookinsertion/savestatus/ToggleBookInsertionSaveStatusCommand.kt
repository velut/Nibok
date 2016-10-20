package com.nibokapp.nibok.domain.command.bookinsertion.savestatus

import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command

/**
 * Toggle the save status of the book insertion with the given id.
 *
 * @param insertionId the id of the insertion
 *
 * @return true if the insertion was saved, false if it was unsaved
 */
class ToggleBookInsertionSaveStatusCommand(
        val insertionId: String,
        val bookRepository: BookInsertionRepositoryInterface = BookInsertionRepository
) : Command<Boolean> {

    override fun execute(): Boolean =
            bookRepository.toggleInsertionSaveStatus(insertionId)
}