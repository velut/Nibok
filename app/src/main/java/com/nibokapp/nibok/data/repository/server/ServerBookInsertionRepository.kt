package com.nibokapp.nibok.data.repository.server

import android.util.Log
import com.baasbox.android.BaasUser
import com.baasbox.android.BaasUser.current
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.extension.getPublishedInsertions
import com.nibokapp.nibok.extension.getSavedInsertions
import com.nibokapp.nibok.extension.toggleInsertionSaveStatus
import com.nibokapp.nibok.server.fetch.ServerDataFetcher
import com.nibokapp.nibok.server.fetch.common.ServerDataFetcherInterface
import com.nibokapp.nibok.server.mapper.ServerDataMapper
import com.nibokapp.nibok.server.mapper.common.ServerDataMapperInterface
import java.util.*

/**
 * Server repository for book insertions.
 */
object ServerBookInsertionRepository: BookInsertionRepositoryInterface {

    const private val TAG = "ServerBookInsertionRepo"

    // Fetcher and mapper used to retrieve data from the server
    // and map it into data for the local db
    private val fetcher: ServerDataFetcherInterface = ServerDataFetcher()
    private val mapper: ServerDataMapperInterface = ServerDataMapper()

    // Current logged in user
    private val currentUser: BaasUser?
        get() = current()

    // Caches
    private var feedCache: List<Insertion> = emptyList()
    private var savedCache: List<Insertion> = emptyList()
    private var publishedCache: List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getInsertionById(insertionId: String) : Insertion? {
        val result = fetcher.fetchInsertionDocumentById(insertionId)
        return result?.let { mapper.convertDocumentToInsertion(it) }
    }

    override fun getBookByISBN(isbn: String): Book? {
        val result = fetcher.fetchBookDocumentFromISBN(isbn)
        return result?.let { mapper.convertDocumentToBook(it) }
    }

    override fun getInsertionListFromQuery(query: String) : List<Insertion> {
        val result = fetcher.fetchInsertionDocumentListByQuery(query)
        val insertions = mapper.convertDocumentListToInsertions(result)
        Log.d(TAG, "Found ${insertions.size} insertions corresponding to query: $query")
        return insertions
    }

    override fun getInsertionListAfterDate(date: Date) : List<Insertion> {
        val result = fetcher.fetchInsertionDocumentListAfterDate(date)
        val insertions = mapper.convertDocumentListToInsertions(result)
        Log.d(TAG, "Found ${insertions.size} insertions after date: $date")
        return insertions
    }

    override fun getInsertionListBeforeDate(date: Date) : List<Insertion> {
        val result = fetcher.fetchInsertionDocumentListBeforeDate(date)
        val insertions = mapper.convertDocumentListToInsertions(result)
        Log.d(TAG, "Found ${insertions.size} insertions before date: $date")
        return insertions
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        val result = fetcher.fetchRecentInsertionDocumentList()
        feedCache = mapper.convertDocumentListToInsertions(result).excludeUserOwnInsertions()
        Log.d(TAG, "Found ${feedCache.size} feed insertions")
        return feedCache
    }

    override fun getFeedInsertionListFromQuery(query: String) : List<Insertion>  =
            getInsertionListFromQuery(query).excludeUserOwnInsertions()

    override fun getFeedInsertionListAfterDate(date: Date) : List<Insertion> =
            getInsertionListAfterDate(date).excludeUserOwnInsertions()

    override fun getFeedInsertionListBeforeDate(date: Date) : List<Insertion> =
            getInsertionListBeforeDate(date).excludeUserOwnInsertions()

    /*
     * SAVED BOOK INSERTIONS
     */

    override fun getSavedInsertionList(cached: Boolean) : List<Insertion> {
        if (cached) return savedCache
        savedCache = currentUser?.getSavedInsertions() ?: emptyList()
        return savedCache
    }

    override fun getSavedInsertionListFromQuery(query: String) : List<Insertion> =
            getInsertionListFromQuery(query).includeOnlySavedInsertions()

    override fun getSavedInsertionLisAfterDate(date: Date) : List<Insertion> =
            getInsertionListAfterDate(date).includeOnlySavedInsertions()

    override fun getSavedInsertionListBeforeDate(date: Date) : List<Insertion> =
            getInsertionListBeforeDate(date).includeOnlySavedInsertions()

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedInsertionList(cached: Boolean) : List<Insertion> {
        if (cached) return publishedCache
        publishedCache = currentUser?.getPublishedInsertions() ?: emptyList()
        return publishedCache
    }

    override fun getPublishedInsertionListFromQuery(query: String) : List<Insertion> =
            getInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    override fun getPublishedInsertionListAfterDate(date: Date) : List<Insertion> =
            getInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    override fun getPublishedInsertionListBeforeDate(date: Date) : List<Insertion> =
            getInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: String) : Boolean =
            insertionId in getSavedInsertionList().map { it.id }

    override fun toggleInsertionSaveStatus(insertionId: String) : Boolean {
        val user = currentUser ?:
                throw IllegalStateException("No user logged in. Cannot toggle insertion save status")

        val saved = user.toggleInsertionSaveStatus(insertionId)
        Log.d(TAG, "After toggle: Save status: $saved")
        return saved
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishInsertion(insertion: Insertion) : Boolean =
            // TODO
            throw UnsupportedOperationException()


    private fun List<Insertion>.excludeUserOwnInsertions() : List<Insertion> {
        val externalInsertions = currentUser?.let {
            val userId = it.name
            this.filter { it.seller?.username != userId }
        }
        return externalInsertions ?: this
    }

    private fun List<Insertion>.includeOnlyUserOwnInsertions() : List<Insertion> {
        val ownInsertions = currentUser?.let {
            val userId = it.name
            this.filter { it.seller?.username == userId }
        }
        return ownInsertions ?: this
    }

    private fun List<Insertion>.includeOnlySavedInsertions() : List<Insertion> {
        val savedInsertions = getSavedInsertionList()
        return this.filter { it.id in savedInsertions.map { it.id } }
    }
}
