package com.nibokapp.nibok.domain.command.bookinsertion.saved

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request a cached version of the BookInsertionModel instances
 * that make up the saved books insertions.
 */
class RequestCachedSavedBookInsertionCommand: Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}