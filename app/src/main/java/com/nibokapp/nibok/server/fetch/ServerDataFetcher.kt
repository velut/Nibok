package com.nibokapp.nibok.server.fetch

import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasQuery
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.repository.server.common.ServerCollection
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.onSuccessReturn
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface

/**
 * ServerDataFetcher implements the ServerDataFetcherInterface and fetches data from the server.
 */
class ServerDataFetcher : ServerDataFetcherInterface {

    /*
     * USER
     */

    override fun fetchUserFromId(userId: String) : BaasUser? {
        val user = BaasUser.fetchSync(userId).onSuccessReturn { it }
        return user
    }

    /*
     * BOOK DATA
     */

    override fun fetchBookDocumentFromISBN(isbn: String) : BaasDocument? {
        val query = BaasQuery.builder()
                .where("${ServerConstants.ISBN}=$isbn")
                .criteria()

        val result = BaasDocument.fetchAllSync(ServerCollection.BOOKS.id, query)

        val book = result.onSuccessReturn { it.getOrNull(0) }
        return book
    }

    /*
     * INSERTIONS
     */

    override fun fetchInsertionDocuments(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentsFromCollection(idsArray, ServerCollection.INSERTIONS)
    }

    /*
     * CONVERSATIONS
     */

    override fun fetchConversationDocuments(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentsFromCollection(idsArray, ServerCollection.CONVERSATIONS)
    }

    /*
     * MESSAGES
     */

    override fun fetchMessageDocuments(idsArray: JsonArray) : List<BaasDocument> {
        return fetchDocumentsFromCollection(idsArray, ServerCollection.MESSAGES)
    }

    private fun fetchDocumentsFromCollection(idsArray: JsonArray, collection: ServerCollection) :
            List<BaasDocument> {
        return idsArray.filterIsInstance<String>() // Get the strings representing documents ids
                .map { BaasDocument.fetchSync(collection.id, it) } // Fetch docs from the collection
                .filter { it.isSuccess } // Keep only successful requests
                .map { it.value() } // Extract the BaasDocument
                .filterNotNull() // Filter eventually null values
    }
}