package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.extension.*
import io.realm.Case
import java.util.*

/**
 * Repository for conversations.
 */
object ConversationRepository : ConversationRepositoryInterface {
    
    const private val TAG = "ConversationRepository"

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
        Log.d(TAG, "Conversations corresponding to query '$query' = ${results.size}")
        return results
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        conversationCache =
                userRepository.getLocalUser()?.conversations?.toNormalList() ?: emptyList()
        Log.d(TAG, "Found ${conversationCache.size} Conversations")
        return conversationCache
    }

    override fun getConversationListAfterDate(date: Date): List<Conversation> {

        if (!userRepository.localUserExists()) return emptyList()

        val results = queryManyRealm {
            it.where(Conversation::class.java)
                    .equalTo("userId", userRepository.getLocalUserId())
                    .greaterThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} Conversations after $date")
        return results
    }

    override fun getConversationListBeforeDate(date: Date): List<Conversation> {

        if (!userRepository.localUserExists()) return emptyList()

        val results = queryManyRealm {
            it.where(Conversation::class.java)
                    .equalTo("userId", userRepository.getLocalUserId())
                    .lessThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} Conversations before $date")
        return results
    }

    override fun startConversation(partnerId: String): String? {

        if (!userRepository.localUserExists()) return null

        val localUserId = userRepository.getLocalUserId()

        // Prevent conversations between the user and himself
        if (localUserId == partnerId) return null

        var conversationId: String? = null

        executeRealmTransaction {
            val conversation = it.getConversationBetweenUsers(localUserId, partnerId)
            if (conversation != null) {
                conversationId = conversation.id
            } else {
                val conversationPartner =
                        it.getExternalUserById(partnerId) ?: return@executeRealmTransaction

                // TODO get values from server
                val newConversation = it.createObject(Conversation::class.java)
                newConversation.apply {
                    id = Random().nextLong().toString()
                    userId = localUserId
                    partner = conversationPartner
                    date = Date()
                }
                conversationId = newConversation.id
            }
        }
        return conversationId
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
        var sent = false
        executeRealmTransaction {
            val conversation = it.getConversationById(message.conversationId)
            conversation?.let {
                it.messages.add(message)
                sent = true
            }
        }
        return sent
    }
}