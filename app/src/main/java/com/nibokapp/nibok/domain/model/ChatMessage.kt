package com.nibokapp.nibok.domain.model

import java.util.*

/**
 * Schema representing chat messages to be displayed in chat bubbles.
 *
 * @property conversationId the id of the conversation between user and partner
 * @property senderId the id of the user who sent the message
 * @property text the text content of the message
 * @property date the date in which the message was sent
 */
data class ChatMessage(
        val conversationId: Long,
        val senderId: Long,
        val text: String,
        val date: Date
)