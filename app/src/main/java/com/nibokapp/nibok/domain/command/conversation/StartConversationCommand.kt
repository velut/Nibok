package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command

/**
 * Start a conversation with the partner with the given id.
 *
 * @param partnerId the id of the partner
 *
 * @return the id of the started conversation or null if the conversation could not be started
 */
class StartConversationCommand(
        val partnerId: String,
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<String?> {

    override fun execute(): String? =
            conversationRepository.startConversation(partnerId)
}