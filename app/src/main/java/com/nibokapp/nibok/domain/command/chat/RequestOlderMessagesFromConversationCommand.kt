package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Request the list of ChatMessageModel instances that represent messages arrived before the
 * given message.
 *
 * @param firstMessage the first message before the requested older messages
 *
 * @return messages dated before the given one
 */
class RequestOlderMessagesFromConversationCommand(
        val firstMessage: ChatMessageModel,
        val dataMapper: MessageDataMapperInterface = MessageDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<List<ChatMessageModel>> {

    override fun execute(): List<ChatMessageModel> =
            dataMapper.convertMessageListToDomain(
                    conversationRepository.getMessageListBeforeDateOfMessage(
                            firstMessage.id
                    )
            )
}