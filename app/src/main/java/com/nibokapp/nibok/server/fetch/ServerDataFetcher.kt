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
    }

    /*
     * USER
     */

    override fun fetchUserById(userId: String) : BaasUser? {
        val user = BaasUser.fetchSync(userId).onSuccessReturn { it }
        return user
    }

    override fun fetchUserAvatar(username: String) : String? {
        val result = BaasBox.rest().sync(
                Rest.Method.GET,
                "plugin/users.getUserAvatar?username=$username"
        )
        return result.onSuccessReturn { it.getString(ServerConstants.DATA) }
    }

    /*
     * BOOK DATA
     */

    override fun fetchBookDocumentById(bookId: String): BaasDocument? {
        val book = BaasDocument.fetchSync(COLL_BOOKS.id, bookId).onSuccessReturn { it }
        return book
    }

    override fun fetchBookDocumentByISBN(isbn: String) : BaasDocument? {
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

    override fun fetchInsertionDocumentListById(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_INSERTIONS)
    }

    override fun fetchInsertionDocumentById(id: String): BaasDocument? {
        val result = BaasDocument.fetchSync(COLL_INSERTIONS.id, id)
        return result.onSuccessReturn { it }
    }

    override fun fetchInsertionDocumentListByQuery(query: String): List<BaasDocument> {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val whereString = with(ServerConstants) {
                "$TITLE like $query or " +
                "$query in $AUTHORS or " +
                "$PUBLISHER like $query or " +
                "$ISBN like $query"
        }
        return queryDocumentListFromCollection(COLL_INSERTIONS, whereString)
    }

    override fun fetchInsertionDocumentListAfterDate(date: Date): List<BaasDocument> {
        val whereString = "${ServerConstants.DATE} >= ${date.toStringDate()}"
        return queryDocumentListFromCollection(COLL_INSERTIONS, whereString)
    }

    override fun fetchInsertionDocumentListBeforeDate(date: Date): List<BaasDocument> {
        val whereString = "${ServerConstants.DATE} <= ${date.toStringDate()}"
        return queryDocumentListFromCollection(COLL_INSERTIONS, whereString)
    }
/*
     * CONVERSATIONS
     */

    override fun fetchConversationDocumentListById(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_CONVERSATIONS)
    }

    /*
     * MESSAGES
     */

    override fun fetchMessageDocumentList(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentListFromCollectionById(idsArray, COLL_MESSAGES)
    }

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
                .orderBy(ServerConstants.DATE)
                .criteria()
        val result = BaasDocument.fetchAllSync(collection.id, criteria)
        return result.onSuccessReturn { it } ?: emptyList()
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
        return idsArray.filterIsInstance<String>() // Get the strings representing documents ids
                .map { BaasDocument.fetchSync(collection.id, it) } // Fetch docs from the collection
                .filter { it.isSuccess } // Keep only successful requests
                .map { it.value() } // Extract the BaasDocument
                .filterNotNull() // Filter eventually null values
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
                                                whereConditions: String) : List<BaasDocument> {
        val criteria = BaasQuery.builder().where(whereConditions).criteria()
        val result = BaasDocument.fetchAllSync(collection.id, criteria)
        return result.onSuccessReturn { it } ?: emptyList()
    }
}