package com.nibokapp.nibok.domain.command.conversation

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.ConversationDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.ConversationDataMapperInterface
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Request a list of ConversationModel instances dated after the given conversation.
 *
 * @param firstConversation the first conversation before the newer ones
 */
class RequestNewerConversationCommand(
        val firstConversation: ConversationModel,
        val dataMapper: ConversationDataMapperInterface = ConversationDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) :
        Command<List<ConversationModel>> {

        override fun execute(): List<ConversationModel> =
                dataMapper.convertConversationListToDomain(
                        conversationRepository.getConversationListAfterDate(
                                firstConversation.date
                        )
                )
}