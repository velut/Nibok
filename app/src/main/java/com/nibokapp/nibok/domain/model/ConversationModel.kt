package com.nibokapp.nibok.domain.model

import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import java.util.*

/**
 * Schema representing essential information about a message conversation
 * between the local user and an external user (partner).
 *
 * @param conversationId the id of the conversation in which messages are exchanged
 * @param userId the id of the local user
 * @param partner the conversation's partner
 * @param previewText the short text previewing the latest message in the conversation
 * @param partnerHasReplied true if the latest message comes from the partner, false otherwise
 * @param date the date in which the conversation was last updated
 */
data class ConversationModel(
        val conversationId: String,
        val userId: String,
        val partner: UserModel,
        val previewText: String,
        val partnerHasReplied: Boolean,
        val date: Date
) : ViewType {

    override fun getItemId(): String = conversationId

    override fun getViewType(): Int = ViewTypes.MESSAGE
}