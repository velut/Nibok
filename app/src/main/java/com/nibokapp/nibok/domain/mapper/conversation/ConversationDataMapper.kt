package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.domain.mapper.user.UserMapper
import com.nibokapp.nibok.domain.mapper.user.UserMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.domain.model.UserModel
import com.nibokapp.nibok.extension.toNormalList
import com.nibokapp.nibok.extension.toRealmList

/**
 * Conversation data mapper implementation.
 */
class ConversationDataMapper(
        val userMapper: UserMapperInterface = UserMapper(),
        val messageMapper: MessageDataMapperInterface = MessageDataMapper()
) : ConversationDataMapperInterface {
    
    override fun convertConversationListToDomain(conversations: List<Conversation>): List<ConversationModel> =
            conversations.map { convertConversationToDomain(it) }.filterNotNull()

    override fun convertConversationToDomain(conversation: Conversation?): ConversationModel? {

        if (conversation == null || !conversation.isWellFormed()) {
            return null
        } else {
            return with(conversation) {
                ConversationModel(
                        conversationId = id,
                        userId = userId,
                        partner = convertPartnerToDomain(partner!!),
                        date = date!!,
                        previewText = messages.lastOrNull()?.text ?: "",
                        chatMessages = convertMessageListToDomain(messages.toNormalList())
                )
            }
        }
    }

    override fun convertConversationListFromDomain(conversations: List<ConversationModel>):
            List<Conversation> = conversations.map { convertConversationFromDomain(it) }

    override fun convertConversationFromDomain(conversation: ConversationModel): Conversation = with(conversation) {
        Conversation(
                id = conversationId,
                userId = userId,
                partner = convertPartnerFromDomain(partner),
                date = date,
                messages = convertMessageListFromDomain(chatMessages).toRealmList()
        )
    }

    private fun convertPartnerToDomain(partner: ExternalUser): UserModel =
            userMapper.convertUserToDomain(partner)

    private fun convertPartnerFromDomain(partner: UserModel): ExternalUser =
            userMapper.convertUserFromDomain(partner)

    private fun  convertMessageListToDomain(messages: List<Message>): List<ChatMessageModel> =
            messageMapper.convertMessageListToDomain(messages)

    private fun  convertMessageListFromDomain(messages: List<ChatMessageModel>): List<Message> =
            messageMapper.convertMessageListFromDomain(messages)
}
