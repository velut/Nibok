package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Message data mapper implementation.
 */
class MessageDataMapper : MessageDataMapperInterface {

    override fun convertMessageListToDomain(messages: List<Message>): List<ChatMessageModel> =
        messages.map { convertMessageToDomain(it) }.filterNotNull()

    override fun convertMessageToDomain(message: Message?): ChatMessageModel? {
        if (message == null || !message.isWellFormed()) {
            return null
        } else {
            return with(message) {
                ChatMessageModel(
                        conversationId,
                        senderId,
                        text,
                        date!!
                )
            }
        }
    }

    override fun convertMessageListFromDomain(messages: List<ChatMessageModel>): List<Message> =
        messages.map { convertMessageFromDomain(it) }

    override fun convertMessageFromDomain(message: ChatMessageModel): Message = with(message) {
        Message(
                conversationId,
                senderId,
                text,
                date
        )
    }

    private fun Message.isWellFormed() = this.date != null
}
