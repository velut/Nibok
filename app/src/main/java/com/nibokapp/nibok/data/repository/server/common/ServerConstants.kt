package com.nibokapp.nibok.data.repository.server.common

/**
 * Constants to interact with JSON objects coming from the server.
 */
object ServerConstants {

    /**
     * Common attributes.
     */
    const val ID = "id"
    const val DATA = "data"
    const val DISTINCT = "distinct"
    const val CREATION_DATE = "_creation_date"
    const val AUTHOR = "_author"

    /**
     * Username.
     */
    const val USERNAME_AVAILABLE = "available"

    /**
     * User attributes.
     */
    const val SAVED_INSERTIONS = "savedInsertions"
    const val PUBLISHED_INSERTIONS = "publishedInsertions"
    const val CONVERSATIONS = "conversations"
    const val AVATAR = "avatar"

    /**
     * Book attributes.
     */
    const val TITLE = "title"
    const val AUTHORS = "authors"
    const val YEAR = "year"
    const val PUBLISHER = "publisher"
    const val ISBN = "isbn"

    /**
     * Insertion attributes.
     */
    const val BOOK_ID = "bookId"
    const val BOOK_PRICE = "bookPrice"
    const val BOOK_CONDITION = "bookCondition"
    const val BOOK_PICTURES = "bookPictures"

    /**
     * Conversation attributes.
     */
    const val PARTICIPANTS = "participants"
    const val MESSAGES = "messages"

    /**
     * Message attributes.
     */
    const val CONVERSATION_ID = "conversationId"
    const val SENDER_ID = "senderId"
    const val TEXT = "text"
}

/**
 * Collections of documents present on the server.
 */
enum class ServerCollection(val id: String) {
    INSERTIONS("INSERTIONS"),
    CONVERSATIONS("CONVERSATIONS"),
    BOOKS("BOOKS"),
    MESSAGES("MESSAGES")
}