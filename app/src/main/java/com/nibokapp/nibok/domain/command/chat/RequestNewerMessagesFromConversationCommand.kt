package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Request the list of ChatMessageModel instances that represent messages arrived after the
 * given message.
 *
 * @param lastMessage the last message before the requested newer messages
 */
class RequestNewerMessagesFromConversationCommand(val lastMessage: ChatMessageModel) :
        Command<List<ChatMessageModel>> {

    override fun execute(): List<ChatMessageModel> = emptyList()
}