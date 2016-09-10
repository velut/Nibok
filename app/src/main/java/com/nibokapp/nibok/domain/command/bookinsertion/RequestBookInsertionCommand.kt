package com.nibokapp.nibok.domain.command.bookinsertion

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instance corresponding to the given book insertion id.
 *
 * @param insertionId the id of the book insertion
 *
 * @return a BookInsertionModel representing the insertion with the given id
 * if such insertion exists, null if no such insertion exists
 */
class RequestBookInsertionCommand(val insertionId: Long) : Command<BookInsertionModel?> {

    override fun execute(): BookInsertionModel? = null
}