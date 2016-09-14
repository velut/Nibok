package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.domain.model.ConversationModel

/**
 * Interface for Data Mappers operating on conversations.
 */
interface ConversationDataMapperInterface {

    /**
     * Build a list of ConversationModel given a list of conversations from the DB.
     * Conversations with invalid data are dropped from the returned list.
     *
     * @param conversations the list of conversations obtained from the DB
     *
     * @return a list of ConversationModel instances
     */
    fun convertConversationListToDomain(conversations: List<Conversation>) : List<ConversationModel>

    /**
     * Build a ConversationModel from a DB conversation.
     *
     * @param conversation the considered conversation
     *
     * @return a ConversationModel instance if conversation data is complete, null otherwise
     */
    fun convertConversationToDomain(conversation: Conversation?) : ConversationModel?

    /**
     * Build a list of Conversation given a list of domain conversations.
     *
     * @param conversations the domain conversations
     *
     * @return a list of Conversation
     */
    fun convertConversationListFromDomain(conversations: List<ConversationModel>) : List<Conversation>

    /**
     * Build a Conversation given a domain conversation.
     *
     * @param conversation the domain conversation
     *
     * @return a Conversation
     */
    fun convertConversationFromDomain(conversation: ConversationModel) : Conversation

}