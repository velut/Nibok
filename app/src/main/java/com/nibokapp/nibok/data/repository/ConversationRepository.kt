package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalConversationRepository
import com.nibokapp.nibok.data.repository.server.ServerConversationRepository
import com.nibokapp.nibok.extension.firstListResultOrNull
import com.nibokapp.nibok.extension.firstResultOrNull

/**
 * Repository for conversations.
 */
object ConversationRepository : ConversationRepositoryInterface {
    
    const private val TAG = "ConversationRepository"

    /**
     * Sources for this repository
     */
    private val localRepository = LocalConversationRepository
    private val serverRepository = ServerConversationRepository
    private val SOURCES = listOf(localRepository, serverRepository)

    private var conversationCache: List<Conversation> = emptyList()


    override fun getConversationById(conversationId: String): Conversation? =
            SOURCES.firstResultOrNull { it.getConversationById(conversationId) }

    override fun getConversationPartnerName(conversationId: String): String? =
            SOURCES.firstResultOrNull { it.getConversationPartnerName(conversationId) }

    override fun getConversationPreviewText(conversationId: String): String? =
            SOURCES.firstResultOrNull { it.getConversationPreviewText(conversationId) }

    override fun getConversationListFromQuery(query: String): List<Conversation> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = SOURCES.firstListResultOrNull { it.getConversationListFromQuery(trimmedQuery) }
                ?: emptyList()

        // TODO store
        Log.d(TAG, "Conversations corresponding to query '$trimmedQuery' = ${results.size}")
        return results
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        conversationCache = SOURCES.firstListResultOrNull { it.getConversationList(cached) }
                ?: emptyList()

        // TODO Store
        Log.d(TAG, "Found ${conversationCache.size} conversations")
        return conversationCache
    }

    override fun startConversation(partnerId: String): String? {
        Log.d(TAG, "Starting conversation with user: $partnerId")
        return SOURCES.firstResultOrNull { it.startConversation(partnerId) }
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListForConversation(conversationId) }
                ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId")
        return messages
    }

    override fun getMessageListBeforeDateOfMessage(messageId: String): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListBeforeDateOfMessage(messageId) } ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages older than message: $messageId")
        return messages
    }

    override fun getMessageListAfterDateOfMessage(messageId: String): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListAfterDateOfMessage(messageId) } ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages newer than message: $messageId")
        return messages
    }

    override fun sendMessage(message: Message): String? {
        return serverRepository.sendMessage(message)
    }
}