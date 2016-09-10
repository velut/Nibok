package com.nibokapp.nibok.domain.command.bookinsertion.published

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instances that make up insertions published by the local user.
 */
class RequestPublishedBookInsertionCommand : Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}