package com.nibokapp.nibok.domain.model

import java.util.*

/**
 * Schema representing chat messages to be displayed in chat bubbles.
 *
 * @param conversationId the id of the conversation between user and partner
 * @param senderId the id of the user who sent the message
 * @param text the text content of the message
 * @param date the date in which the message was sent
 */
data class ChatMessageModel(
        val conversationId: Long,
        val senderId: Long,
        val text: String,
        val date: Date
)