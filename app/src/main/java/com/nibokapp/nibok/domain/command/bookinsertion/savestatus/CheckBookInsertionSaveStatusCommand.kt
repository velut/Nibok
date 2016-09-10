package com.nibokapp.nibok.domain.command.bookinsertion.savestatus

import com.nibokapp.nibok.domain.command.common.Command

/**
 * Check if the book insertion with the given id is saved or not.
 *
 * @param insertionId the id of the insertion
 *
 * @return true if the insertion is saved, false if it is not saved
 */
class CheckBookInsertionSaveStatusCommand(val insertionId: Long) : Command<Boolean> {

    override fun execute(): Boolean = false
}