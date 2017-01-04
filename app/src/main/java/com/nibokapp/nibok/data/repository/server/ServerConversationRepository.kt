package com.nibokapp.nibok.data.repository.server

import android.util.Log
import com.baasbox.android.BaasUser
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.server.fetch.ServerDataFetcher
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import com.nibokapp.nibok.server.mapper.ServerDataMapper
import com.nibokapp.nibok.server.mapper.common.ServerDataMapperInterface
import com.nibokapp.nibok.server.send.ServerDataSender
import com.nibokapp.nibok.server.send.common.ServerDataSenderInterface
import java.util.*

/**
 * Server repository for conversations.
 */
object ServerConversationRepository : ConversationRepositoryInterface {

    private const val TAG = "ServerConversationRepo"

    // Fetcher and sender used to exchange conversation data with the server
    private val fetcher: ServerDataFetcherInterface = ServerDataFetcher()
    private val sender: ServerDataSenderInterface = ServerDataSender()

    // Mapper used to map data exchanged with the server
    private val mapper: ServerDataMapperInterface = ServerDataMapper()

    // Current logged in user
    private val currentUser: BaasUser?
        get() = BaasUser.current()

    // Caches
    private var conversationCache: List<Conversation> = emptyList()

    override fun getConversationById(conversationId: String): Conversation? {
        Log.d(TAG, "Getting conversation by id: $conversationId")
        val result = fetcher.fetchConversationDocumentById(conversationId)
        return result?.let { mapper.convertDocumentToConversation(it) }
    }

    override fun getConversationPartnerName(conversationId: String): String? {
        Log.d(TAG, "Getting partner's name for conversation: $conversationId")
        val conversation = getConversationById(conversationId)
        return conversation?.partner?.username
    }

    override fun getConversationListFromQuery(query: String): List<Conversation> {
        Log.d(TAG, "Getting conversation list for query: $query")
        val result = fetcher.fetchConversationDocumentListByQuery(query)
        val conversations = mapper.convertDocumentListToConversations(result)
        Log.d(TAG, "Found ${conversations.size} conversations corresponding to query: $query")
        return conversations
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        val result = fetcher.fetchRecentConversationDocumentList()
        conversationCache = mapper.convertDocumentListToConversations(result)
        Log.d(TAG, "Found ${conversationCache.size} recent conversations")
        return conversationCache
    }

    override fun getConversationListAfterDate(date: Date): List<Conversation> {
        Log.d(TAG, "Getting conversations after date: $date")
        val result = fetcher.fetchConversationDocumentListAfterDate(date)
        val conversations = mapper.convertDocumentListToConversations(result)
        Log.d(TAG, "Found ${conversations.size} conversations after date: $date")
        return conversations
    }

    override fun getConversationListBeforeDate(date: Date): List<Conversation> {
        Log.d(TAG, "Getting conversations before date: $date")
        val result = fetcher.fetchConversationDocumentListBeforeDate(date)
        val conversations = mapper.convertDocumentListToConversations(result)
        Log.d(TAG, "Found ${conversations.size} conversations before date: $date")
        return conversations
    }

    override fun startConversation(partnerId: String): String? {
        TODO()
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        Log.d(TAG, "Getting messages for conversation: $conversationId")
        val result = fetcher.fetchMessageDocumentListByConversation(conversationId)
        val messages = mapper.convertDocumentListToMessages(result)
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId")
        return messages
    }

    override fun getMessageListAfterDateForConversation(conversationId: String, date: Date): List<Message> {
        Log.d(TAG, "Getting messages for conversation: $conversationId after date: $date")
        val result = fetcher.fetchMessageDocumentListAfterDateByConversation(conversationId, date)
        val messages = mapper.convertDocumentListToMessages(result)
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId after date: $date")
        return messages
    }

    override fun getMessageListBeforeDateForConversation(conversationId: String, date: Date): List<Message> {
        Log.d(TAG, "Getting messages for conversation: $conversationId before date: $date")
        val result = fetcher.fetchMessageDocumentListBeforeDateByConversation(conversationId, date)
        val messages = mapper.convertDocumentListToMessages(result)
        Log.d(TAG, "Found ${messages.size} messages for conversation: $conversationId before date: $date")
        return messages
    }

    override fun sendMessage(message: Message): Boolean {
        TODO()
    }
}