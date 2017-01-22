package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.domain.mapper.user.UserMapper
import com.nibokapp.nibok.domain.mapper.user.UserMapperInterface
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.domain.model.UserModel

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

        if (conversation == null || !conversation.isWellFormed()) return null

        return with(conversation) {
            val partner = convertPartnerToDomain(partner!!)
            val previewText = latestMessage?.text ?: ""
            val partnerHasReplied = latestMessage?.senderId == partner.username
            ConversationModel(
                    id,
                    userId,
                    partner,
                    previewText,
                    partnerHasReplied,
                    date!!
            )
        }
    }
    // TODO 2 unused override methods V
    override fun convertConversationListFromDomain(conversations: List<ConversationModel>):
            List<Conversation> = conversations.map { convertConversationFromDomain(it) }

    override fun convertConversationFromDomain(conversation: ConversationModel): Conversation = with(conversation) {
        val partner = convertPartnerFromDomain(partner)
        val latestMessage = null
        Conversation(
                conversationId,
                userId,
                partner,
                latestMessage,
                date
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
