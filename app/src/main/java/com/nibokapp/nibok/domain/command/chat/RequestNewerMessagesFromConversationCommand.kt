package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Request the list of ChatMessageModel instances that represent messages arrived after the
 * given message.
 *
 * @param lastMessage the last message before the requested newer messages
 *
 * @return messages dated after the given one
 */
class RequestNewerMessagesFromConversationCommand(
        val lastMessage: ChatMessageModel,
        val dataMapper: MessageDataMapperInterface = MessageDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<List<ChatMessageModel>> {

    override fun execute(): List<ChatMessageModel> =
            dataMapper.convertMessageListToDomain(
                    conversationRepository.getMessageListAfterDateOfMessage(
                            lastMessage.id
                    )
            )
}