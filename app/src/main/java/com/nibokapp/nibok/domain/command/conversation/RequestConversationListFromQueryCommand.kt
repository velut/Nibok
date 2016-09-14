package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.ConversationDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.ConversationDataMapperInterface
import com.nibokapp.nibok.domain.model.ConversationModel


/**
 * Request a list of ConversationModel instances that make up the conversations in which
 * the local user is participating and matching the given query.
 *
 * @param query the string describing the query
 */
class RequestConversationListFromQueryCommand(
        val query: String,
        val dataMapper: ConversationDataMapperInterface = ConversationDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<List<ConversationModel>> {

    override fun execute(): List<ConversationModel> =
            dataMapper.convertConversationListToDomain(
                    conversationRepository.getConversationListFromQuery(query)
            )
}