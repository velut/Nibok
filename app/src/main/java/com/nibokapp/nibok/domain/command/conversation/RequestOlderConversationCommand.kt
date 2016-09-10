package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Request a list of ConversationModel instances dated before the given conversation.
 *
 * @param lastConversation the last conversation before the older ones
 */
class RequestOlderConversationCommand(val lastConversation: ConversationModel) :
        Command<List<ConversationModel>> {

    override fun execute(): List<ConversationModel> = emptyList()
}