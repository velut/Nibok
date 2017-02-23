package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.domain.mapper.user.UserMapper
import com.nibokapp.nibok.domain.mapper.user.UserMapperInterface
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.domain.model.UserModel

/**
 * Conversation data mapper implementation.
 */
class ConversationDataMapper(
        val userMapper: UserMapperInterface = UserMapper()
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
                    lastUpdateDate!!
            )
        }
    }

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

}
