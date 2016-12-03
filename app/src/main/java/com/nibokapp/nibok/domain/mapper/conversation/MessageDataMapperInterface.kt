package com.nibokapp.nibok.domain.mapper.conversation

import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.domain.model.ChatMessageModel

/**
 * Interface for Data Mappers operating on messages.
 */
interface MessageDataMapperInterface {

    /**
     * Build a list of ChatMessageModel given a list of messages from the DB.
     * Messages with invalid data are dropped from the returned list.
     *
     * @param messages the list of messages obtained from the DB
     *
     * @return a list of ChatMessageModel instances
     */
    fun convertMessageListToDomain(messages: List<Message>): List<ChatMessageModel>

    /**
     * Build a ChatMessageModel from a DB message.
     *
     * @param message the considered message
     *
     * @return a ChatMessageModel instance if message data is complete, null otherwise
     */
    fun convertMessageToDomain(message: Message?): ChatMessageModel?

    /**
     * Build a list of Message given a list of domain messages.
     *
     * @param messages the domain messages
     *
     * @return a list of Message
     */
    fun convertMessageListFromDomain(messages: List<ChatMessageModel>): List<Message>

    /**
     * Build a Message given a domain message.
     *
     * @param message the domain message
     *
     * @return a Message
     */
    fun convertMessageFromDomain(message: ChatMessageModel): Message

}