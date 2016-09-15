package com.nibokapp.nibok.domain.command.chat

import com.nibokapp.nibok.data.repository.ConversationRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapper
import com.nibokapp.nibok.domain.mapper.conversation.MessageDataMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Send a chat message.
 *
 * @param message the message to send
 *
 * @return true if the message was sent successfully, false otherwise
 */
class SendMessageCommand(
        val message: ChatMessageModel,
        val dataMapper: MessageDataMapperInterface = MessageDataMapper(),
        val conversationRepository: ConversationRepositoryInterface = ConversationRepository
) : Command<Boolean> {

    override fun execute(): Boolean =
            conversationRepository.sendMessage(
                    dataMapper.convertMessageFromDomain(
                            message
                    )
            )
}