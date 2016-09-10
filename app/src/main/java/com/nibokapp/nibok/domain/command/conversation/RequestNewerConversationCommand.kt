package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Request a list of ConversationModel instances dated after the given conversation.
 *
 * @param firstConversation the first conversation before the newer ones
 */
class RequestNewerConversationCommand(val firstConversation: ConversationModel) :
        Command<List<ConversationModel>> {

        override fun execute(): List<ConversationModel> = emptyList()
}