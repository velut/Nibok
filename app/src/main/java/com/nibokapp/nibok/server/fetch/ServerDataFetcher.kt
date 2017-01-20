package com.nibokapp.nibok.server.fetch

import com.baasbox.android.*
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.repository.server.common.ServerCollection
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.onSuccessReturn
import com.nibokapp.nibok.extension.toStringDate
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import java.util.*

/**
 * ServerDataFetcher implements the ServerDataFetcherInterface and fetches data from the server.
 */
class ServerDataFetcher : ServerDataFetcherInterface {

    companion object {
        private val COLL_INSERTIONS = ServerCollection.INSERTIONS
        private val COLL_BOOKS = ServerCollection.BOOKS
        private val COLL_CONVERSATIONS = ServerCollection.CONVERSATIONS
        private val COLL_MESSAGES = ServerCollection.MESSAGES

        private val RECORDS_PER_PAGE = 30

        /**
         * Functions building Strings representing common queries to the server database
         */
        fun CONVERSATION_ID_EQUALS(id: String) = "${ServerConstants.CONVERSATION_ID}=\"$id\""
        fun ID_NOT_EQUALS(id: String) = "${ServerConstants.ID}<>\"$id\""
        fun ID_IN_LIST(list: String) = "${ServerConstants.ID} in $list"
        fun CREATION_DATE_AFTER(date: String) = "${ServerConstants.CREATION_DATE} >= date('$date')"
        fun CREATION_DATE_BEFORE(date: String) = "${ServerConstants.CREATION_DATE} <= date('$date')"
        fun LIST_OF(items: List<String>)= items.joinToString(", ", "[", "]") { "\"$it\""}
        fun AND(vararg items: String) = items.joinToString(" and ")
        fun OR(vararg items: String) = items.joinToString(" or ")
    }

    /*
     * USER
     */

    override fun fetchUserById(userId: String): BaasUser? {
        return BaasUser.fetchSync(userId).onSuccessReturn { it }
    }

    override fun fetchUserAvatar(username: String): String? {
        val result = BaasBox.rest().sync(
                Rest.Method.GET,
                "plugin/users.getUserAvatar?username=$username"
        )
        return result.onSuccessReturn { it.getString(ServerConstants.DATA) }
    }

    /*
     * BOOK DATA
     */

    override fun fetchBookDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_BOOKS, id)
    }

    override fun fetchBookDocumentByISBN(isbn: String): BaasDocument? {
        val whereString = "${ServerConstants.ISBN}=$isbn"
        val bookDocList = queryDocumentListFromCollection(COLL_BOOKS, whereString)
        return bookDocList.getOrNull(0) // Get the first book in the list
    }

    /*
     * INSERTIONS
     */

    override fun fetchRecentInsertionDocumentList(): List<BaasDocument> {
        return fetchRecentDocumentListFromCollection(COLL_INSERTIONS)
    }

    override fun fetchInsertionDocumentListById(idsArray: JsonArray): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_INSERTIONS)
    }

    override fun fetchInsertionDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_INSERTIONS, id)
    }

    override fun fetchInsertionDocumentListByQuery(query: String): List<BaasDocument> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val whereString = with(ServerConstants) {
            OR(
                    "$TITLE like $query",
                    "$query in $AUTHORS",
                    "$PUBLISHER like $query",
                    "$ISBN like $query"
            )
        }
        return queryDocumentListFromCollection(COLL_INSERTIONS, whereString)
    }

    override fun fetchInsertionDocumentListAfterDate(date: Date): List<BaasDocument> {
        return queryDocumentListFromCollection(COLL_INSERTIONS, getAfterDateQueryCondition(date))
    }

    override fun fetchInsertionDocumentListBeforeDate(date: Date): List<BaasDocument> {
        return queryDocumentListFromCollection(COLL_INSERTIONS, getBeforeDateQueryCondition(date))
    }

    /*
     * CONVERSATIONS
     */

    override fun fetchConversationDocumentListById(idsArray: JsonArray): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_CONVERSATIONS)
    }

    override fun fetchConversationDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_CONVERSATIONS, id)
    }

    override fun fetchConversationDocumentByParticipants(firstParticipantId: String, secondParticipantId: String): BaasDocument? {
        val whereString = with(ServerConstants) {
            "\"$firstParticipantId\" in $PARTICIPANTS and \"$secondParticipantId\" in $PARTICIPANTS"
        } // e.g. ""bob" in participants and "sam" in participants"
        return queryDocumentListFromCollection(COLL_CONVERSATIONS, whereString).getOrNull(0)
    }

    override fun fetchConversationDocumentListByQuery(query: String): List<BaasDocument> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val whereString = with(ServerConstants) {
            "" // TODO?
        }

        return queryDocumentListFromCollection(COLL_CONVERSATIONS, whereString)
    }

    override fun fetchRecentConversationDocumentList(): List<BaasDocument> {
        return fetchRecentDocumentListFromCollection(COLL_CONVERSATIONS)
    }

    override fun fetchConversationDocumentListAfterDate(date: Date): List<BaasDocument> {
        return queryDocumentListFromCollection(COLL_INSERTIONS, getAfterDateQueryCondition(date))
    }

    override fun fetchConversationDocumentListBeforeDate(date: Date): List<BaasDocument> {
        return queryDocumentListFromCollection(COLL_INSERTIONS, getBeforeDateQueryCondition(date))
    }

    /*
     * MESSAGES
     */

    override fun fetchMessageDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_MESSAGES, id)
    }

    override fun fetchMessageDocumentList(idsArray: JsonArray): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_MESSAGES)
    }

    override fun fetchMessageDocumentListByConversation(conversationId: String): List<BaasDocument> {
        val whereString = CONVERSATION_ID_EQUALS(conversationId)
        return queryDocumentListFromCollection(COLL_MESSAGES, whereString)
    }

    override fun fetchMessageDocumentListBeforeDateOfMessage(messageId: String): List<BaasDocument> {
        return fetchMessageDocumentListByDateOfMessage(messageId, true)
    }

    override fun fetchMessageDocumentListAfterDateOfMessage(messageId: String): List<BaasDocument> {
        return fetchMessageDocumentListByDateOfMessage(messageId, false)
    }

    /*
     * OTHER
     */

    /**
     * Fetch recent documents from the server belonging to the specified collection.
     * The returned list of document is ordered by date.
     *
     * @param collection the collection from which the documents are fetched
     *
     * @return a list of BaasDocument
     */
    private fun fetchRecentDocumentListFromCollection(collection: ServerCollection)
           : List<BaasDocument>{
        val criteria = BaasQuery.builder()
                .pagination(0, RECORDS_PER_PAGE)
                .orderBy(ServerConstants.DATE) // TODO Fix
                .criteria()
        val result = BaasDocument.fetchAllSync(collection.id, criteria)
        return result.onSuccessReturn { it } ?: emptyList()
    }

    private fun fetchDocumentFromCollectionById(collection: ServerCollection, id: String): BaasDocument? {
        return BaasDocument.fetchSync(collection.id, id).onSuccessReturn { it }
    }

    /**
     * Fetch the list of documents with the ids given in the array from the specified collection.
     *
     * @param idsArray the array containing the ids of the documents to fetch
     * @param collection the collection from which the documents are fetched
     *
     * @return a list of BaasDocument
     */
    private fun fetchDocumentListFromCollectionById(idsArray: JsonArray,
                                                    collection: ServerCollection)
           : List<BaasDocument> {
        val idList = idsArray.filterIsInstance<String>()
        val whereString = ID_IN_LIST(LIST_OF(idList))
        return queryDocumentListFromCollection(collection, whereString)
    }

    /**
     * Query documents from the specified collection matching the given where clause.
     *
     * @param collection the collection from which the documents are fetched
     * @param whereConditions the String representing the where clause
     *
     * @return a list of BaasDocument
     */
    private fun queryDocumentListFromCollection(collection: ServerCollection,
                                                whereConditions: String): List<BaasDocument> {
        val criteria = BaasQuery.builder().where(whereConditions).criteria()
        val result = BaasDocument.fetchAllSync(collection.id, criteria)
        return result.onSuccessReturn { it } ?: emptyList()
    }

    /**
     * Get the String that describes the where condition
     * for the date field to be after the given date.
     */
    private fun getAfterDateQueryCondition(date: Date): String {
        return "${ServerConstants.DATE} >= ${date.toStringDate()}"
    }

    /**
     * Get the String that describes the where condition
     * for the date field to be before the given date.
     */
    private fun getBeforeDateQueryCondition(date: Date): String {
        return "${ServerConstants.DATE} <= ${date.toStringDate()}"
    }

    private fun fetchMessageDocumentListByDateOfMessage(messageId: String, beforeDate: Boolean): List<BaasDocument> {
        val message = fetchMessageDocumentById(messageId) ?: return emptyList()
        val conversationId = message.getString(ServerConstants.CONVERSATION_ID) ?: return emptyList()
        val messageDate = message.creationDate
        val dateCondition = if (beforeDate) CREATION_DATE_BEFORE(messageDate) else CREATION_DATE_AFTER(messageDate)
        val whereString = AND(
                CONVERSATION_ID_EQUALS(conversationId),
                ID_NOT_EQUALS(messageId),
                dateCondition
        )
        return queryDocumentListFromCollection(COLL_MESSAGES, whereString)
    }
}