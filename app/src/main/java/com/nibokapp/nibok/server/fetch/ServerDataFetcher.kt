package com.nibokapp.nibok.server.fetch

import com.baasbox.android.*
import com.baasbox.android.BaasUser.current
import com.nibokapp.nibok.data.repository.server.common.ServerCollection
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.getSavedInsertionsIdList
import com.nibokapp.nibok.extension.onSuccessReturn
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface

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
         * Functions building Strings representing common queries to the server database.
         */
        // Common
        fun QUOTE(attribute: String) = "\"$attribute\""
        fun ATTR_IN_LIST(attribute: String, list: String) = "$attribute in $list"

        // Document Id
        fun ID_NOT_EQUALS(id: String) = "${ServerConstants.ID}<>${QUOTE(id)}"
        fun ID_IN_LIST(list: String) = ATTR_IN_LIST(ServerConstants.ID, list)

        // Document Author
        fun AUTHOR_EQUALS(authorId: String) = "${ServerConstants.AUTHOR}=${QUOTE(authorId)}"
        fun AUTHOR_NOT_EQUALS(authorId: String) = "${ServerConstants.AUTHOR}<>${QUOTE(authorId)}"

        // Insertion
        fun BOOK_ID_IN_LIST(list: String) = ATTR_IN_LIST(ServerConstants.BOOK_ID, list)

        // Isbn
        fun ISBN_EQUALS(isbn: String) = "${ServerConstants.ISBN}=$isbn"

        // Conversation
        fun CONVERSATION_ID_EQUALS(id: String) = "${ServerConstants.CONVERSATION_ID}=${QUOTE(id)}"

        fun LAST_UPDATE_NOT_EMPTY() = "${ServerConstants.LAST_UPDATE_DATE}<>\"\""
        fun LAST_UPDATE_BEFORE(date: String) = "${ServerConstants.LAST_UPDATE_DATE} <= date('$date')"

        // Document Creation's date
        fun CREATION_DATE_AFTER(date: String) = "${ServerConstants.CREATION_DATE} >= date('$date')"
        fun CREATION_DATE_BEFORE(date: String) = "${ServerConstants.CREATION_DATE} <= date('$date')"

        // Operators
        fun AND(vararg items: String) = items.joinToString(" and ")
        fun OR(vararg items: String) = items.joinToString(" or ")
        fun IN(one: String, other: String) = "$one in $other"
        fun LIKE(one: String, other: String) = "$one like \"%$other%\""
        fun LIST_OF_ID(items: List<String>)= items.joinToString(", ", "[", "]") { QUOTE(it) }

        // Ordering
        fun ORDER_BY_DESC(field: String) = "$field desc"
        fun ORDER_BY_DESC_CREATION_DATE() = ORDER_BY_DESC(ServerConstants.CREATION_DATE)
        fun ORDER_BY_DESC_LAST_UPDATE_DATE() = ORDER_BY_DESC(ServerConstants.LAST_UPDATE_DATE)
    }

    /*
     * USER
     */

    private val currentUser: BaasUser?
        get() = current()

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
        val whereString = ISBN_EQUALS(isbn)
        val bookDocList = queryDocumentListFromCollection(COLL_BOOKS, whereString)
        return bookDocList.getOrNull(0) // Get the first book in the list
    }

    /*
     * INSERTIONS
     */

    override fun fetchRecentInsertionDocumentList(filterByCurrentUser: Boolean,
                                                  excludeAllByUser: Boolean,
                                                  includeOnlyIfSaved: Boolean,
                                                  includeOnlyByUser: Boolean): List<BaasDocument> {

        if (!filterByCurrentUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS)
        }

        val user = currentUser ?: if (excludeAllByUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS)
        } else {
            return emptyList()
        }

        if (excludeAllByUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, AUTHOR_NOT_EQUALS(user.name))
        }

        if (includeOnlyByUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, AUTHOR_EQUALS(user.name))
        }

        if (includeOnlyIfSaved) {
            val savedInsertionIds = user.getSavedInsertionsIdList()
            return queryDocumentListFromCollection(COLL_INSERTIONS, ID_IN_LIST(LIST_OF_ID(savedInsertionIds)))
        }

        return emptyList()
    }

    override fun fetchInsertionDocumentListById(idList: List<String>): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idList, COLL_INSERTIONS)
    }

    override fun fetchInsertionDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_INSERTIONS, id)
    }

    override fun fetchInsertionDocumentListByQuery(query: String,
                                                   filterByCurrentUser: Boolean,
                                                   excludeAllByUser: Boolean,
                                                   includeOnlyIfSaved: Boolean,
                                                   includeOnlyByUser: Boolean): List<BaasDocument> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return emptyList()

        // First retrieve the books corresponding to the query and extract their ids
        val bookWhereString = with(ServerConstants) {
            OR(
                    LIKE(TITLE, query),
                    IN(QUOTE(query), AUTHORS),
                    LIKE(PUBLISHER, query),
                    LIKE(ISBN, query)
            )
        }
        val bookIds = queryDocumentListFromCollection(COLL_BOOKS, bookWhereString).map { it.id }
        if (bookIds.isEmpty()) return emptyList()

        // Then query the insertions in which the found books are sold
        val relevantInsertions = BOOK_ID_IN_LIST(LIST_OF_ID(bookIds))

        if (!filterByCurrentUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, relevantInsertions)
        }

        val user = currentUser ?: if (excludeAllByUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, relevantInsertions)
        } else {
            return emptyList()
        }

        if (excludeAllByUser) {
            val relevantFiltered = AND(relevantInsertions, AUTHOR_NOT_EQUALS(user.name))
            return queryDocumentListFromCollection(COLL_INSERTIONS, relevantFiltered)
        }

        if (includeOnlyByUser) {
            val relevantFiltered = AND(relevantInsertions, AUTHOR_EQUALS(user.name))
            return queryDocumentListFromCollection(COLL_INSERTIONS, relevantFiltered)
        }

        if (includeOnlyIfSaved) {
            val savedInsertionIds = user.getSavedInsertionsIdList()
            val relevantFiltered = AND(relevantInsertions, ID_IN_LIST(LIST_OF_ID(savedInsertionIds)))
            return queryDocumentListFromCollection(COLL_INSERTIONS, relevantFiltered)
        }

        return emptyList()
    }

    override fun fetchInsertionDocumentListAfterDateOfInsertion(insertionId: String,
                                                                filterByCurrentUser: Boolean,
                                                                excludeAllByUser: Boolean,
                                                                includeOnlyIfSaved: Boolean,
                                                                includeOnlyByUser: Boolean): List<BaasDocument> {
        val insertion = fetchInsertionDocumentById(insertionId) ?: return emptyList()
        val insertionDate = insertion.creationDate
        val olderInsertions = AND(
                ID_NOT_EQUALS(insertionId),
                CREATION_DATE_BEFORE(insertionDate)
        )

        if (!filterByCurrentUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, olderInsertions)
        }

        val user = currentUser ?: if (excludeAllByUser) {
            return queryDocumentListFromCollection(COLL_INSERTIONS, olderInsertions)
        } else {
            return emptyList()
        }

        if (excludeAllByUser) {
            val olderInsertionsFiltered = AND(olderInsertions, AUTHOR_NOT_EQUALS(user.name))
            return queryDocumentListFromCollection(COLL_INSERTIONS, olderInsertionsFiltered)
        }

        if (includeOnlyByUser) {
            val olderInsertionsFiltered = AND(olderInsertions, AUTHOR_EQUALS(user.name))
            return queryDocumentListFromCollection(COLL_INSERTIONS, olderInsertionsFiltered)
        }

        if (includeOnlyIfSaved) {
            val savedInsertionIds = user.getSavedInsertionsIdList()
            val olderInsertionsFiltered = AND(olderInsertions, ID_IN_LIST(LIST_OF_ID(savedInsertionIds)))
            return queryDocumentListFromCollection(COLL_INSERTIONS, olderInsertionsFiltered)
        }

        return emptyList()
    }

    /*
     * CONVERSATIONS
     */

    override fun fetchConversationDocumentListById(idList: List<String>): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idList, COLL_CONVERSATIONS)
    }

    override fun fetchConversationDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_CONVERSATIONS, id)
    }

    override fun fetchConversationDocumentByParticipants(firstParticipantId: String, secondParticipantId: String): BaasDocument? {
        val whereString = with(ServerConstants) {
            AND(
                    IN(QUOTE(firstParticipantId), PARTICIPANTS),
                    IN(QUOTE(secondParticipantId), PARTICIPANTS)
            )
        } // e.g. ""bob" in participants and "sam" in participants"
        return queryDocumentListFromCollection(COLL_CONVERSATIONS, whereString).getOrNull(0)
    }

    override fun fetchConversationDocumentListByQuery(query: String): List<BaasDocument> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return emptyList()

        val whereString = with(ServerConstants) {
            IN(QUOTE(trimmedQuery), PARTICIPANTS)
        }
        return queryDocumentListFromCollection(COLL_CONVERSATIONS, whereString)
    }

    override fun fetchRecentConversationDocumentList(): List<BaasDocument> {
        return queryDocumentListFromCollection(COLL_CONVERSATIONS,
                LAST_UPDATE_NOT_EMPTY(),
                ORDER_BY_DESC_LAST_UPDATE_DATE())
    }

    override fun fetchConversationDocumentListOlderThanConversation(conversationId: String): List<BaasDocument> {
        val conversationDocument = fetchConversationDocumentById(conversationId) ?: return emptyList()
        val lastUpdateDate = conversationDocument.getString(ServerConstants.LAST_UPDATE_DATE)
        if (lastUpdateDate == null || lastUpdateDate == "") {
            return emptyList()
        }
        val order = ORDER_BY_DESC_LAST_UPDATE_DATE()
        val whereCondition = AND(
                ID_NOT_EQUALS(conversationId),
                LAST_UPDATE_NOT_EMPTY(),
                LAST_UPDATE_BEFORE(lastUpdateDate)
        )
        return queryDocumentListFromCollection(COLL_CONVERSATIONS, whereCondition, order)
    }

    /*
     * MESSAGES
     */

    override fun fetchMessageDocumentById(id: String): BaasDocument? {
        return fetchDocumentFromCollectionById(COLL_MESSAGES, id)
    }

    override fun fetchMessageDocumentList(idList: List<String>): List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idList, COLL_MESSAGES)
    }

    override fun fetchMessageDocumentListByConversation(conversationId: String): List<BaasDocument> {
        val whereString = CONVERSATION_ID_EQUALS(conversationId)
        return queryDocumentListFromCollection(COLL_MESSAGES, whereString).reversed()
    }

    override fun fetchMessageDocumentListBeforeDateOfMessage(messageId: String): List<BaasDocument> {
        return fetchMessageDocumentListByDateOfMessage(messageId, true).reversed()
    }

    override fun fetchMessageDocumentListAfterDateOfMessage(messageId: String): List<BaasDocument> {
        return fetchMessageDocumentListByDateOfMessage(messageId, false).reversed()
    }

    override fun fetchLatestMessageByConversation(conversationId: String): BaasDocument? {
        return queryDocumentListFromCollection(COLL_MESSAGES,
                CONVERSATION_ID_EQUALS(conversationId),
                recordsPerPage = 1).getOrNull(0)
    }

    /*
     * OTHER
     */

    private fun fetchDocumentFromCollectionById(collection: ServerCollection, id: String): BaasDocument? {
        return BaasDocument.fetchSync(collection.id, id).onSuccessReturn { it }
    }

    /**
     * Fetch the list of documents with the ids given in the list from the specified collection.
     *
     * @param idList the list of id values
     * @param collection the collection from which the documents are fetched
     *
     * @return a list of BaasDocument
     */
    private fun fetchDocumentListFromCollectionById(idList: List<String>,
                                                    collection: ServerCollection)
           : List<BaasDocument> {
        val whereString = ID_IN_LIST(LIST_OF_ID(idList))
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
                                                whereConditions: String? = null,
                                                order: String = ORDER_BY_DESC_CREATION_DATE(),
                                                page: Int = 0,
                                                recordsPerPage: Int = RECORDS_PER_PAGE): List<BaasDocument> {
        val query = BaasQuery.builder()
                .pagination(page, recordsPerPage)
                .orderBy(order)
        whereConditions?.let { query.where(it) }

        val criteria = query.criteria()
        val result = BaasDocument.fetchAllSync(collection.id, criteria)
        return result.onSuccessReturn { it } ?: emptyList()
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