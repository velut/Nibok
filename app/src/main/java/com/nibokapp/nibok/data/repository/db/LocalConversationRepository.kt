package com.nibokapp.nibok.data.repository.db

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.extension.queryManyRealm
import com.nibokapp.nibok.extension.queryOneRealm
import com.nibokapp.nibok.extension.toNormalList
import io.realm.Case
import java.util.*

/**
 * Local repository for conversations.
 */
object LocalConversationRepository : ConversationRepositoryInterface {

    private const val TAG = "LocalConversationRepo"

    private val userRepository: UserRepositoryInterface = UserRepository

    private var conversationCache: List<Conversation> = emptyList()


    override fun getConversationById(conversationId: String): Conversation? {
        if (userRepository.localUserExists()) {
            return queryOneRealm {
                it.where(Conversation::class.java)
                        .equalTo("id", conversationId)
                        .equalTo("userId", userRepository.getLocalUserId())
                        .findFirst()
            }
        } else {
            return null
        }
    }

    override fun getConversationPartnerName(conversationId: String): String? =
            getConversationById(conversationId)?.partner?.username

    override fun getConversationListFromQuery(query: String): List<Conversation> {

        if (!userRepository.localUserExists()) return emptyList()

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = queryManyRealm {
            it.where(Conversation::class.java)
                    .equalTo("userId", userRepository.getLocalUserId())
                    .contains("partner.name", trimmedQuery, Case.INSENSITIVE)
                    .or()
                    .contains("messages.text", trimmedQuery, Case.INSENSITIVE)
                    .findAll()
        }
        Log.d(TAG, "Conversations corresponding to query '$trimmedQuery' = ${results.size}")
        return results
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        conversationCache =
                userRepository.getLocalUser()?.conversations?.toNormalList() ?: emptyList()
        Log.d(TAG, "Found ${conversationCache.size} conversations")
        return conversationCache
    }

    override fun getConversationListAfterDate(date: Date): List<Conversation> {

        if (!userRepository.localUserExists()) return emptyList()

        val results = queryManyRealm {
            it.where(Conversation::class.java)
                    .equalTo("userId", userRepository.getLocalUserId())
                    .greaterThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} conversations after $date")
        return results
    }

    override fun getConversationListBeforeDate(date: Date): List<Conversation> {

        if (!userRepository.localUserExists()) return emptyList()

        val results = queryManyRealm {
            it.where(Conversation::class.java)
                    .equalTo("userId", userRepository.getLocalUserId())
                    .lessThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} conversations before $date")
        return results
    }

    override fun startConversation(partnerId: String): String? {
        throw UnsupportedOperationException()
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        val conversation = getConversationById(conversationId)
        return conversation?.messages?.toNormalList() ?: emptyList()
    }

    override fun getMessageListAfterDateForConversation(conversationId: String, date: Date): List<Message> {
        return getMessageListForConversation(conversationId)
                .sortedBy { it.date }
                .filter { it.date!! >= date }
    }

    override fun getMessageListBeforeDateForConversation(conversationId: String, date: Date): List<Message> {
        return getMessageListForConversation(conversationId)
                .sortedBy { it.date }
                .filter { it.date!! <= date }
    }

    override fun sendMessage(message: Message): Boolean {
        throw UnsupportedOperationException()
    }
}