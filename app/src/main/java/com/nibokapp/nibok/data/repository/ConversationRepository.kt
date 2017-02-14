package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalConversationRepository
import com.nibokapp.nibok.data.repository.server.ServerConversationRepository
import com.nibokapp.nibok.extension.firstListResultOrNullWithStorage
import com.nibokapp.nibok.extension.firstResultOrNull
import com.nibokapp.nibok.extension.storeAndReturnResult
import io.realm.RealmModel

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


    /*
     * Conversation
     */

    override fun getConversationById(conversationId: String): Conversation? {
        return SOURCES.firstResultOrNull { it.getConversationById(conversationId) }
                .storeAndReturnResult { c, s -> storeInLocalRepo(c, s) }
    }

    override fun getConversationPartnerName(conversationId: String): String? {
        return SOURCES.firstResultOrNull { it.getConversationPartnerName(conversationId) }.first
    }

    override fun getConversationPreviewText(conversationId: String): String? {
        return SOURCES.firstResultOrNull { it.getConversationPreviewText(conversationId) }.first
    }

    override fun getConversationListFromQuery(query: String): List<Conversation> {
        val results = SOURCES.firstListResultOrNullWithLocalStorage {
            it.getConversationListFromQuery(query)
        } ?: emptyList()
        val conversations = results.filterIsInstance<Conversation>()
        Log.d(TAG, "Conversations corresponding to query '$query' = ${conversations.size}")
        return conversations
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        val results = SOURCES.reversed().firstListResultOrNullWithLocalStorage {
            it.getConversationList(cached)
        } ?: emptyList()
        conversationCache = results.filterIsInstance<Conversation>()
        Log.d(TAG, "Found ${conversationCache.size} conversations")
        return conversationCache
    }

    override fun getConversationListOlderThanConversation(conversationId: String): List<Conversation> {
        val results = SOURCES.firstListResultOrNullWithLocalStorage {
            it.getConversationListOlderThanConversation(conversationId)
        } ?: emptyList()
        return results.filterIsInstance<Conversation>()
    }

    override fun startConversation(partnerId: String): String? {
        Log.d(TAG, "Starting conversation with user: $partnerId")
        return SOURCES.firstResultOrNull { it.startConversation(partnerId) }.first
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        val results = SOURCES.reversed().firstListResultOrNullWithLocalStorage {
            it.getMessageListForConversation(conversationId)
        } ?: emptyList()
        val messages = results.filterIsInstance<Message>()
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId")
        return messages
    }

    override fun getMessageListBeforeDateOfMessage(messageId: String): List<Message> {
        val results = SOURCES.firstListResultOrNullWithLocalStorage {
            it.getMessageListBeforeDateOfMessage(messageId)
        } ?: emptyList()
        val messages = results.filterIsInstance<Message>()
        Log.d(TAG, "Found ${messages.size} messages older than message: $messageId")
        return messages
    }

    override fun getMessageListAfterDateOfMessage(messageId: String): List<Message> {
        val results = SOURCES.firstListResultOrNullWithLocalStorage {
            it.getMessageListAfterDateOfMessage(messageId)
        } ?: emptyList()
        val messages = results.filterIsInstance<Message>()
        Log.d(TAG, "Found ${messages.size} messages newer than message: $messageId")
        return messages
    }

    override fun sendMessage(message: Message): String? {
        return serverRepository.sendMessage(message)
    }

    /*
     * Utilities
     */

    private fun storeInLocalRepo(item: RealmModel?, source: ConversationRepositoryInterface?) {
        if (source == serverRepository && item != null) {
            localRepository.storeItem(item)
        }
    }

    private fun storeInLocalRepo(itemList: List<RealmModel>?, source: ConversationRepositoryInterface?) {
        if (source == serverRepository && itemList != null) {
            localRepository.storeItems(itemList)
        }
    }

    private inline fun Iterable<ConversationRepositoryInterface>.firstListResultOrNullWithLocalStorage(
            predicate: (ConversationRepositoryInterface) -> List<RealmModel>?): List<RealmModel>? {
        return this.firstListResultOrNullWithStorage(
                { predicate(it) },
                { resultList, source -> storeInLocalRepo(resultList, source) }
        )
    }
}