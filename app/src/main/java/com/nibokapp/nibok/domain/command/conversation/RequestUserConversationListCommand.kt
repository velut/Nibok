package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Request a list of ConversationModel instances that make up the conversations in which
 * the local user is participating.
 */
class RequestUserConversationListCommand : Command<List<ConversationModel>> {

    override fun execute(): List<ConversationModel> = emptyList()
}