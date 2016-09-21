package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalBookInsertionRepository
import com.nibokapp.nibok.data.repository.server.ServerBookInsertionRepository
import com.nibokapp.nibok.extension.*
import java.util.*

/**
 * Repository for book insertions.
 */
object BookInsertionRepository : BookInsertionRepositoryInterface {

    const private val TAG = "BookInsertionRepository"

    /**
     * Sources for this repository
     */
    private val localRepository = LocalBookInsertionRepository
    private val serverRepository = ServerBookInsertionRepository
    private val SOURCES = listOf(localRepository, serverRepository)

    private var feedCache : List<Insertion> = emptyList()
    private var savedCache : List<Insertion> = emptyList()
    private var publishedCache : List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getBookInsertionById(insertionId: String) : Insertion? =
            SOURCES.firstResultOrNull { it.getBookInsertionById(insertionId) }

    override fun getBookByISBN(isbn: String): Book? =
            SOURCES.firstResultOrNull { it.getBookByISBN(isbn) }

    override fun getBookInsertionListFromQuery(query: String) : List<Insertion> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = SOURCES.firstListResultOrNull { it.getBookInsertionListFromQuery(query) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Book insertions corresponding to query '$query' = ${results.size}")

        return results
    }

    override fun getBookInsertionListAfterDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstListResultOrNull { it.getBookInsertionListAfterDate(date) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    override fun getBookInsertionListBeforeDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstListResultOrNull { it.getBookInsertionListBeforeDate(date) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Found ${results.size} insertions before $date")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedBookInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache

        feedCache = SOURCES.firstListResultOrNull { it.getFeedBookInsertionList(cached) }
                ?: emptyList()

        if (feedCache.isNotEmpty()) localRepository.storeItems(feedCache)

        Log.d(TAG, "Found ${feedCache.size} feed insertions")
        return feedCache
    }

    override fun getFeedBookInsertionListFromQuery(query: String) : List<Insertion>  =
            getBookInsertionListFromQuery(query).excludeUserOwnInsertions()

    override fun getFeedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).excludeUserOwnInsertions()

    override fun getFeedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).excludeUserOwnInsertions()

    /*
     * SAVED BOOK INSERTIONS
     */

    override fun getSavedBookInsertionList(cached: Boolean) : List<Insertion> {
        if (cached) return savedCache

        savedCache = SOURCES.firstListResultOrNull { it.getSavedBookInsertionList(cached) }
                ?: emptyList()

        if (savedCache.isNotEmpty()) localRepository.storeItems(savedCache)

        return savedCache
    }

    override fun getSavedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlySavedInsertions()

    override fun getSavedBookInsertionLisAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlySavedInsertions()

    override fun getSavedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlySavedInsertions()

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedBookInsertionList(cached: Boolean) : List<Insertion> {
        if (cached) return publishedCache

        publishedCache = SOURCES.firstListResultOrNull { it.getPublishedBookInsertionList(cached) }
                ?: emptyList()

        if (publishedCache.isNotEmpty()) localRepository.storeItems(publishedCache)

        return publishedCache
    }

    override fun getPublishedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: String) : Boolean =
            insertionId in getSavedBookInsertionList().map { it.id }

    override fun toggleBookInsertionSaveStatus(insertionId: String) : Boolean {
        val savedOnServer = serverRepository.toggleBookInsertionSaveStatus(insertionId)
        if (savedOnServer) {
            localRepository.toggleBookInsertionSaveStatus(insertionId)
        }
        return savedOnServer
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishBookInsertion(insertion: Insertion) : Boolean =
            // TODO
            throw UnsupportedOperationException()
}