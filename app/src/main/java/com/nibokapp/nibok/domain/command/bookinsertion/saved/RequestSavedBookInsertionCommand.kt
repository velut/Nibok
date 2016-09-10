package com.nibokapp.nibok.domain.command.bookinsertion.saved

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the latest BookInsertionModel instances that make up the saved books insertions.
 */
class RequestSavedBookInsertionCommand: Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}