package com.nibokapp.nibok.domain.command.bookinsertion.feed

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instances that represent book insertions dated before the given
 * one.
 *
 * @param lastBookInsertion the last book insertion before the older ones
 */
class RequestOlderBookInsertionFeedCommand(val lastBookInsertion: BookInsertionModel) :
        Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}