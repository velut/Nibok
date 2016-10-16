package com.nibokapp.nibok.server.fetch.common

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray

/**
 * ServerDataFetcherInterface is an interface for objects fetching data from the server.
 */
interface ServerDataFetcherInterface {

    /**
     * Fetch a user given its id.
     *
     * @param userId the id of the user
     *
     * @return a BaasUser if the user was found, null otherwise
     */
    fun fetchUserFromId(userId: String) : BaasUser?

    /**
     * Fetch a book's BaasDocument given its ISBN code.
     *
     * @param isbn the isbn code of the book
     *
     * @return a BaasDocument if the book was found, null otherwise
     */
    fun fetchBookDocumentFromISBN(isbn: String) : BaasDocument?

    /**
     * Fetch a list of BaasDocument for the insertions' ids present in the given array.
     *
     * @param idsArray a JsonArray of insertions' ids
     *
     * @return a list of BaasDocument for the found insertions
     */
    fun fetchInsertionDocuments(idsArray: JsonArray) : List<BaasDocument>

    /**
     * Fetch a list of BaasDocument for the conversations' ids present in the given array.
     *
     * @param idsArray a JsonArray of conversations' ids
     *
     * @return a list of BaasDocument for the found conversations
     */
    fun fetchConversationDocuments(idsArray: JsonArray) : List<BaasDocument>

    /**
     * Fetch a list of BaasDocument for the messages' ids present in the given array.
     *
     * @param idsArray a JsonArray of messages' ids
     *
     * @return a list of BaasDocument for the found messages
     */
    fun fetchMessageDocuments(idsArray: JsonArray) : List<BaasDocument>
}