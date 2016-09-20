package com.nibokapp.nibok.extension

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasQuery
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.AUTHORS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.AVATAR
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.COLLECTION_BOOKS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.COLLECTION_CONVERSATIONS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.COLLECTION_INSERTIONS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.CONDITION
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.CONVERSATIONS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.ISBN
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.PICTURES
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.PRICE
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.PUBLISHED_INSERTIONS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.PUBLISHER
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.SAVED_INSERTIONS
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.TITLE
import com.nibokapp.nibok.data.repository.server.common.ServerConstants.YEAR

/**
 * Extensions for the Server's repository.
 */

fun BaasUser.getSavedInsertions() : List<Insertion> =
        this.getScope(BaasUser.Scope.PRIVATE)
                .getArray(SAVED_INSERTIONS, JsonArray())
                .fetchInsertions()

fun BaasUser.getPublishedInsertions() : List<Insertion> =
        this.getScope(BaasUser.Scope.PUBLIC)
                .getArray(PUBLISHED_INSERTIONS, JsonArray())
                .fetchInsertions()

fun BaasUser.getConversations() : List<Conversation> =
        this.getScope(BaasUser.Scope.PRIVATE)
                .getArray(CONVERSATIONS, JsonArray())
                .fetchConversations()

fun BaasUser.getAvatar() : String =
        this.getScope(BaasUser.Scope.PUBLIC)
                .getString(AVATAR, "")

fun JsonArray.fetchInsertions() : List<Insertion> {
    return this.filterIsInstance<String>()
            .map { BaasDocument.fetchSync(COLLECTION_INSERTIONS, it) }
            .map { it.value() }
            .filterNotNull()
            .map {
                buildInsertionFromDocument(it)
            }
}


fun buildInsertionFromDocument(document: BaasDocument): Insertion {
    return with(document) {
        Insertion(
                id = id,
                date = creationDate.parseDate(),
                seller = fetchUserFromId(author),
                book = fetchBookFromISBN(getString(ISBN)),
                bookPrice = getFloat(PRICE),
                bookCondition = getString(CONDITION),
                bookImagesSources = getArray(PICTURES)
                        .filterIsInstance<String>().toRealmStringList()
        )
    }
}

fun JsonArray.fetchConversations() : List<Conversation> {
    this.filterIsInstance<String>()
            .map { BaasDocument.fetchSync(COLLECTION_CONVERSATIONS, it) }
            .map { it.value() }
            .filterNotNull()
            .map {
                buildConversationFromDocument(it)
            }

    return emptyList()
}

fun buildConversationFromDocument(document: BaasDocument): Conversation {
    return with(document) {
        Conversation(

        )
    }
}

fun fetchUserFromId(username: String) : ExternalUser? {
    val result = BaasUser.fetchSync(username)
    if (result.isSuccess) {
        val userData = result.value()
        return with(userData) {
            ExternalUser (
                    username,
                    getAvatar()
            )
        }
    } else {
        return null
    }
}

fun fetchBookFromISBN(isbn: String): Book? {
    val query = BaasQuery.builder()
            .collection(COLLECTION_BOOKS)
            .where("isbn=?")
            .whereParams(isbn)
            .build()

    val result = query.querySync()
    if (result.isSuccess) {
        val bookData = result.value()

        if (bookData.isEmpty()) return null

        return with(bookData[1]) {
            Book(
                    getString(TITLE),
                    getArray(AUTHORS).filterIsInstance<String>().toRealmStringList(),
                    getInt(YEAR),
                    getString(PUBLISHER),
                    getString(ISBN)
            )
        }
    } else {
        return null
    }
}