package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command

/**
 * Request the name of the conversation's partner.
 *
 * @param conversationId the id of the conversation
 *
 * @return the String describing the partner's name or null if the partner's name was not found
 */
class RequestConversationPartnerNameCommand(
        val conversationId: String,
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<String?> {

    override fun execute(): String? =
            conversationRepository.getConversationPartnerName(conversationId)
}