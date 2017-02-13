package com.nibokapp.nibok.data.repository.db

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Message
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.ConversationRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.db.common.LocalStorage
import com.nibokapp.nibok.extension.*
import io.realm.Case
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery

/**
 * Local repository for conversations.
 */
object LocalConversationRepository : ConversationRepositoryInterface, LocalStorage<RealmModel> {

    private const val TAG = "LocalConversationRepo"

    private val userRepository: UserRepositoryInterface = UserRepository

    private var conversationCache: List<Conversation> = emptyList()

    /*
     * Conversation
     */

    override fun getConversationById(conversationId: String): Conversation? {
        if (!localUserExists()) return null
        val conversation = queryOneRealm {
            it.whereConversation()
                    .idEqualTo(conversationId)
                    .userIdEqualTo(getLocalUserId())
                    .findFirst()
        }
        Log.d(TAG, "For id: $conversationId found: $conversation")
        return conversation
    }

    override fun getConversationPartnerName(conversationId: String): String? {
        return getConversationById(conversationId)?.partner?.username
    }

    override fun getConversationPreviewText(conversationId: String): String? {
        if (!localUserExists()) return null
        val newestMessage = queryManyRealm {
            it.whereMessage()
                    .conversationIdEqualTo(conversationId)
                    .findAllSortedByDescendingDate()
        }.getOrNull(0)
        return newestMessage?.text
    }

    override fun getConversationListFromQuery(query: String): List<Conversation> {
        if (!localUserExists()) return emptyList()

        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return emptyList()

        val results = queryManyRealm {
            it.queryConversation(trimmedQuery).findAllSortedByDescendingDate()
        }
        Log.d(TAG, "Conversations corresponding to query '$trimmedQuery' = ${results.size}")
        return results
    }

    override fun getConversationList(cached: Boolean): List<Conversation> {
        if (cached) return conversationCache
        conversationCache = queryManyRealm {
            it.whereConversation()
                    .findAllSortedByDescendingDate()
        }
        Log.d(TAG, "Found ${conversationCache.size} conversations")
        return conversationCache
    }

    override fun getConversationListOlderThanConversation(conversationId: String): List<Conversation> {
        val currentOldestDate = getConversationById(conversationId)?.date ?: return emptyList()
        return queryManyRealm {
            it.whereConversation()
                    .olderThan(conversationId, currentOldestDate)
                    .findAllSortedByDescendingDate()
        }
    }

    override fun startConversation(partnerId: String): String? {
        if (!localUserExists()) return null

        val conversation = queryOneRealm {
            it.whereConversation()
                    .userIdEqualTo(getLocalUserId())
                    .partnerIdEqualTo(partnerId)
                    .findFirst()
        }
        return conversation?.id
    }

    /*
     * Messages
     */

    private fun getMessageById(messageId: String): Message? {
        return queryOneRealm {
            it.whereMessage()
                    .idEqualTo(messageId)
                    .findFirst()
        }
    }

    override fun getMessageListForConversation(conversationId: String): List<Message> {
        return queryManyRealm {
            it.whereMessage()
                    .conversationIdEqualTo(conversationId)
                    .findAllSortedByAscendingDate()
        }
    }

    override fun getMessageListBeforeDateOfMessage(messageId: String): List<Message> {
        val oldestMessage = getMessageById(messageId) ?: return emptyList()
        val currentOldestMessageDate = oldestMessage.date ?: return emptyList()
        val conversationId = oldestMessage.conversationId

        return queryManyRealm {
            it.whereMessage()
                    .conversationIdEqualTo(conversationId)
                    .olderThan(messageId, currentOldestMessageDate)
                    .findAllSortedByAscendingDate()
        }
    }

    override fun getMessageListAfterDateOfMessage(messageId: String): List<Message> {
        val newestMessage = getMessageById(messageId) ?: return emptyList()
        val currentNewestMessageDate = newestMessage.date ?: return emptyList()
        val conversationId = newestMessage.conversationId

        return queryManyRealm {
            it.whereMessage()
                    .conversationIdEqualTo(conversationId)
                    .newerThan(messageId, currentNewestMessageDate)
                    .findAllSortedByAscendingDate()
        }
    }

    override fun sendMessage(message: Message): String? {
        throw UnsupportedOperationException()
    }

    /*
     * Local storage
     */

    override fun storeItems(items: List<RealmModel>) {
        if (items.isEmpty()) return

        val conversations = items.filterIsInstance<Conversation>()
        val messages = items.filterIsInstance<Message>()
        if (conversations.isEmpty() && messages.isEmpty()) return

        executeRealmTransaction {
            if (conversations.isNotEmpty()) {
                Log.d(TAG, "Storing conversations: ${conversations.map { it.id }}")
                it.copyToRealmOrUpdate(conversations)
            }
            if (messages.isNotEmpty()) {
                Log.d(TAG, "Storing message: ${messages.map { it.id }}")
                it.copyToRealmOrUpdate(messages)
            }
        }
    }

    override fun storeItem(item: RealmModel) {
        when (item) {
            is Conversation -> {
                Log.d(TAG, "Storing conversation: ${item.id} with partner ${item.partner?.username}")
                storeItemInRealm(item)
            }
            is Message -> {
                Log.d(TAG, "Storing message: ${item.id} with text: ${item.text}")
                storeItemInRealm(item)
            }
            else -> Log.e(TAG, "Cannot store unknown item!")
        }
    }

    private fun storeItemInRealm(item: RealmModel) {
        executeRealmTransaction {
            it.copyToRealmOrUpdate(item)
        }
    }

    /*
     * Utilities
     */

    private fun localUserExists(): Boolean {
        return userRepository.localUserExists()
    }

    private fun getLocalUserId(): String {
        return userRepository.getLocalUserId()
    }

    /*
     * Extensions
     */

    private fun Realm.whereConversation(): RealmQuery<Conversation> {
        return this.where(Conversation::class.java)
    }

    private fun Realm.whereMessage(): RealmQuery<Message> {
        return this.where(Message::class.java)
    }

    fun <T : RealmModel> RealmQuery<T>.userIdEqualTo(id: String): RealmQuery<T> {
        return this.equalTo("userId", id)
    }

    fun <T : RealmModel> RealmQuery<T>.partnerIdEqualTo(id: String): RealmQuery<T> {
        return this.equalTo("partner.username", id)
    }

    fun RealmQuery<Message>.conversationIdEqualTo(id: String): RealmQuery<Message> {
        return this.equalTo("conversationId", id)
    }

    fun Realm.queryConversation(query: String): RealmQuery<Conversation> {
        return this.whereConversation()
                .userIdEqualTo(getLocalUserId())
                .beginGroup()
                .contains("partner.username", query, Case.INSENSITIVE)
                .or()
                .contains("latestMessage.text", query, Case.INSENSITIVE)
                .endGroup()
    }

}