package com.nibokapp.nibok.domain.command.bookinsertion.feed

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Request the BookInsertionModel instances that represent book insertions dated after the given
 * one.
 *
 * @param firstBookInsertion the first book insertion before the newer ones
 */
class RequestNewerBookInsertionFeedCommand(val firstBookInsertion: BookInsertionModel) :
        Command<List<BookInsertionModel>> {

    override fun execute(): List<BookInsertionModel> = emptyList()
}