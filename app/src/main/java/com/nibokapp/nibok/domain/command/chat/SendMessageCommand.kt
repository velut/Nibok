package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Send a chat message.
 *
 * @param message the message to send
 *
 * @return true if the message was sent successfully, false otherwise
 */
class SendMessageCommand(val message: ChatMessageModel) : Command<Boolean> {

    override fun execute(): Boolean = false
}