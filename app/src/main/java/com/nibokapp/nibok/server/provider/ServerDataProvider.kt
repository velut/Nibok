package com.nibokapp.nibok.server.provider

import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.server.fetch.ServerDataFetcher
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import com.nibokapp.nibok.server.mapper.ServerDataMapper
import com.nibokapp.nibok.server.provider.common.ServerDataProviderInterface

/**
 * ServerDataProvider provides data ready to be used by the local db
 * given the ids of objects resident on the server.
 *
 * @param fetcher the fetcher that retrieves data from the server. ServerDataFetcher is the default one
 * @param mapper the mapper that maps data from the server into data for the local db. ServerDataMapper is the default one
 */
class ServerDataProvider(
        private val fetcher: ServerDataFetcherInterface = ServerDataFetcher(),
        private val mapper: ServerDataMapper = ServerDataMapper()
) : ServerDataProviderInterface {

    override fun getInsertionListFromIds(idsArray: JsonArray): List<Insertion> {
        val insertionDocuments = fetcher.fetchInsertionDocumentListById(idsArray.filterIsInstance<String>())
        val insertions = mapper.convertDocumentListToInsertions(insertionDocuments)
        return insertions
    }

    override fun getConversationListFromIds(idsArray: JsonArray): List<Conversation> {
        val conversationDocuments = fetcher.fetchConversationDocumentListById(idsArray.filterIsInstance<String>())
        val conversations = mapper.convertDocumentListToConversations(conversationDocuments)
        return conversations
    }
}