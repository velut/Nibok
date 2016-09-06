package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypes
import java.util.*

/**
 * Schema representing essential information about a message conversation.
 *
 * @param conversationId the id of the conversation in which messages are exchanged
 * @param partnerAvatar the source for the profile picture of the conversation's partner
 * @param partnerName the name of the conversation's partner
 * @param previewText the short text previewing the latest message in the conversation
 * @param date the date in which the conversation was last updated
 */
data class MessageModel(
        val conversationId: Long,
        val partnerAvatar: String,
        val partnerName: String,
        val previewText: String,
        val date: Date
) : ViewType {

    override fun getItemId(): Long = conversationId

    override fun getViewType() : Int = ViewTypes.MESSAGE
}