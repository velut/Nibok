package com.nibokapp.nibok.server.fetch.common

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import java.util.*

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
    fun fetchUserById(userId: String): BaasUser?

    /**
     * Fetch the avatar of the user with the given username.
     *
     * @param username the username of the user
     *
     * @return a String representing the avatar source if the request was successful, null otherwise
     */
    fun fetchUserAvatar(username: String): String?

    /**
     * Fetch a book's BaasDocument given its id.
     * The id is not the book's ISBN code.
     *
     * @param id the id of the book
     *
     * @return a BaasDocument if the book was found, null otherwise
     */
    fun fetchBookDocumentById(id: String): BaasDocument?

    /**
     * Fetch a book's BaasDocument given its ISBN code.
     *
     * @param isbn the isbn code of the book
     *
     * @return a BaasDocument if the book was found, null otherwise
     */
    fun fetchBookDocumentByISBN(isbn: String): BaasDocument?

    /**
     * Fetch recent insertion documents.
     *
     * @return a list of BaasDocument
     */
    fun fetchRecentInsertionDocumentList(): List<BaasDocument>

    /**
     * Fetch a list of BaasDocument for the insertions' ids present in the given array.
     *
     * @param idsArray a JsonArray of insertions' ids
     *
     * @return a list of BaasDocument for the found insertions
     */
    fun fetchInsertionDocumentListById(idsArray: JsonArray): List<BaasDocument>

    /**
     * Fetch an insertion document by its id.
     *
     * @param id the id of the document
     *
     * @return a BaasDocument if the document with the given id was found, null otherwise
     */
    fun fetchInsertionDocumentById(id: String): BaasDocument?

    /**
     * Fetch the list of insertion documents corresponding to the given query.
     *
     * @param query the query text (e.g. book's title)
     *
     * @return a list of BaasDocument
     */
    fun fetchInsertionDocumentListByQuery(query: String): List<BaasDocument>

    /**
     * Fetch the list of insertion documents dated after the given date.
     *
     * @param date the date used as in comparisons
     *
     * @return a list of BaasDocument
     */
    fun fetchInsertionDocumentListAfterDate(date: Date): List<BaasDocument>

    /**
     * Fetch the list of insertion documents dated before the given date.
     *
     * @param date the date used as in comparisons
     *
     * @return a list of BaasDocument
     */
    fun fetchInsertionDocumentListBeforeDate(date: Date): List<BaasDocument>

    /**
     * Fetch a list of BaasDocument for the conversations' ids present in the given array.
     *
     * @param idsArray a JsonArray of conversations' ids
     *
     * @return a list of BaasDocument for the found conversations
     */
    fun fetchConversationDocumentListById(idsArray: JsonArray): List<BaasDocument>

    /**
     * Fetch a conversation document by its id.
     *
     * @param id the id of the document
     *
     * @return a BaasDocument if the document with the given id was found, null otherwise
     */
    fun fetchConversationDocumentById(id: String): BaasDocument?

    /**
     * Fetch a conversation document by its participants.
     *
     * @param firstParticipantId the id of the first participant
     * @param secondParticipantId the id of the second participant
     *
     * @return a BaasDocument if the conversation document between the two participants was found, null otherwise
     */
    fun fetchConversationDocumentByParticipants(firstParticipantId: String, secondParticipantId: String): BaasDocument?

    /**
     * Fetch the list of conversation documents corresponding to the given query.
     *
     * @param query the query text
     *
     * @return a list of BaasDocument
     */
    fun fetchConversationDocumentListByQuery(query: String): List<BaasDocument>

    /**
     * Fetch recent conversation documents.
     *
     * @return a list of BaasDocument
     */
    fun fetchRecentConversationDocumentList(): List<BaasDocument>

    /**
     * Fetch the list of conversation documents dated after the given date.
     *
     * @param date the date used as in comparisons
     *
     * @return a list of BaasDocument
     */
    fun fetchConversationDocumentListAfterDate(date: Date): List<BaasDocument>

    /**
     * Fetch the list of conversation documents dated before the given date.
     *
     * @param date the date used as in comparisons
     *
     * @return a list of BaasDocument
     */
    fun fetchConversationDocumentListBeforeDate(date: Date): List<BaasDocument>

    /**
     * Fetch a message document by its id.
     *
     * @param id the id of the document
     *
     * @return a BaasDocument if the document with the given id was found, null otherwise
     */
    fun fetchMessageDocumentById(id: String): BaasDocument?

    /**
     * Fetch a list of BaasDocument for the messages' ids present in the given array.
     *
     * @param idsArray a JsonArray of messages' ids
     *
     * @return a list of BaasDocument for the found messages
     */
    fun fetchMessageDocumentList(idsArray: JsonArray): List<BaasDocument>

    /**
     * Fetch the list of message documents associated to the given conversation.
     *
     * @param conversationId the id of the conversation
     *
     * @return a list of BaasDocument for the found messages
     */
    fun fetchMessageDocumentListByConversation(conversationId: String): List<BaasDocument>

    /**
     * Fetch the message documents that are older than the message with the given id.
     *
     * @param messageId the id of the message
     *
     * @return a list of messages that are older than the message with the given id
     */
    fun  fetchMessageDocumentListBeforeDateOfMessage(messageId: String): List<BaasDocument>

    /**
     * Fetch the message documents that are newer than the message with the given id.
     *
     * @param messageId the id of the message
     *
     * @return a list of messages that are newer than the message with the given id
     */
    fun  fetchMessageDocumentListAfterDateOfMessage(messageId: String): List<BaasDocument>

}