package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Request the list of ChatMessageModel instances that represent messages arrived before the
 * given message.
 *
 * @param firstMessage the first message before the requested older messages
 */
class RequestOlderMessagesFromConversationCommand(val firstMessage: ChatMessageModel) :
        Command<List<ChatMessageModel>> {

    override fun execute(): List<ChatMessageModel> = emptyList()
}