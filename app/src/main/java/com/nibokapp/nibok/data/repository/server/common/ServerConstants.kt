package com.nibokapp.nibok.data.repository.server.common

/**
 * Constants to interact with JSON objects coming from the server.
 */
object ServerConstants {

    /**
     * Generic data.
     */
    const val DATA = "data"

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
     * Insertion attributes
     */
    const val PRICE = "price"
    const val CONDITION = "condition"
    const val PICTURES = "pictures"


    /**
     * Server collections
     */
    const val COLLECTION_INSERTIONS = "INSERTIONS"
    const val COLLECTION_CONVERSATIONS = "CONVERSATIONS"
    const val COLLECTION_BOOKS = "BOOKS"
}
