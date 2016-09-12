package com.nibokapp.nibok.domain.command.bookinsertion.publishing

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Publish the given book insertion.
 *
 * @param insertion the book insertion to be published
 *
 * @return true if the insertion was published successfully, false otherwise
 */
class PublishBookInsertionCommand(val insertion: BookInsertionModel) : Command<Boolean> {

    override fun execute(): Boolean = false
}