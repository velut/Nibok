package com.nibokapp.nibok.domain.command.user

import com.nibokapp.nibok.domain.command.common.Command

/**
 * Request the id of the local user.
 *
 */
class RequestLocalUserIdCommand : Command<Long> {

    override fun execute(): Long = 0L
}