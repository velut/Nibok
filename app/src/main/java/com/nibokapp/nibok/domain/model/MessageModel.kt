package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.common.AdapterTypes
import com.nibokapp.nibok.ui.adapter.common.ViewType

/**
 * Schema representing essential information about a message conversation.
 *
 * @property conversationId the id of the conversation in which messages are exchanged
 * @property partnerAvatar the source for the profile picture of the conversation's partner
 * @property partnerName the name of the conversation's partner
 * @property previewText the short text previewing the latest message in the conversation
 * @property date the date in which the conversation was last updated
 */
data class MessageModel(
        val conversationId: Long,
        val partnerAvatar: String,
        val partnerName: String,
        val previewText: String,
        val date: String
) : ViewType {
    override fun getViewType() : Int = AdapterTypes.MESSAGE
}