package com.nibokapp.nibok.domain.command.bookinsertion.feed

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request a cached version of BookInsertionModel instances
 * that make up the feed of books insertions.
 */
class RequestCachedBookInsertionFeedCommand: Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}