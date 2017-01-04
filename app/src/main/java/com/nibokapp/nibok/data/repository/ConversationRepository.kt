package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalConversationRepository
import com.nibokapp.nibok.data.repository.server.ServerConversationRepository
import com.nibokapp.nibok.extension.firstListResultOrNull
import com.nibokapp.nibok.extension.firstResultOrNull
import java.util.*

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

    override fun getConversationListAfterDate(date: Date): List<Conversation> {
        val results = SOURCES.firstListResultOrNull { it.getConversationListAfterDate(date) }
                ?: emptyList()
        Log.d(TAG, "Found ${results.size} conversations after $date")
        return results
    }

    override fun getConversationListBeforeDate(date: Date): List<Conversation> {
        val results = SOURCES.firstListResultOrNull { it.getConversationListBeforeDate(date) }
                ?: emptyList()
        Log.d(TAG, "Found ${results.size} conversations before $date")
        return results
    }

    override fun startConversation(partnerId: String): String? {
        return serverRepository.startConversation(partnerId)
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListForConversation(conversationId) }
                ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId")
        return messages
    }

    override fun getMessageListAfterDateForConversation(conversationId: String, date: Date): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListAfterDateForConversation(conversationId, date) }
                ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId after date: $date")
        return messages
    }

    override fun getMessageListBeforeDateForConversation(conversationId: String, date: Date): List<Message> {
        val messages = SOURCES.firstListResultOrNull { it.getMessageListBeforeDateForConversation(conversationId, date) }
                ?: emptyList()
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId before date: $date")
        return messages
    }

    override fun sendMessage(message: Message): Boolean {
        return serverRepository.sendMessage(message)
    }
}