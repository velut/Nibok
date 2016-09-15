package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Request the list of messages for the conversation with the given id.
 *
 * @param conversationId the id of the conversation
 *
 * @return the list of ChatMessageModel messages belonging to the conversation with the given id
 */
class RequestMessagesFromConversationCommand(
        val conversationId: Long,
        val dataMapper: MessageDataMapperInterface = MessageDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<List<ChatMessageModel>> {

    override fun execute(): List<ChatMessageModel> =
            dataMapper.convertMessageListToDomain(
                    conversationRepository.getMessageListForConversation(
                            conversationId
                    )
            )

}