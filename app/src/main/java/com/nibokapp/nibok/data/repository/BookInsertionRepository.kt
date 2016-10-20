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

    override fun getInsertionById(insertionId: String) : Insertion? =
            SOURCES.firstResultOrNull { it.getInsertionById(insertionId) }

    override fun getBookByISBN(isbn: String): Book? =
            SOURCES.firstResultOrNull { it.getBookByISBN(isbn) }

    override fun getInsertionListFromQuery(query: String) : List<Insertion> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = SOURCES.firstListResultOrNull { it.getInsertionListFromQuery(query) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Book insertions corresponding to query '$query' = ${results.size}")

        return results
    }

    override fun getInsertionListAfterDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstListResultOrNull { it.getInsertionListAfterDate(date) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    override fun getInsertionListBeforeDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstListResultOrNull { it.getInsertionListBeforeDate(date) }
                ?: emptyList()

        if (results.isNotEmpty()) localRepository.storeItems(results)

        Log.d(TAG, "Found ${results.size} insertions before $date")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache

        feedCache = SOURCES.firstListResultOrNull { it.getFeedInsertionList(cached) }
                ?: emptyList()

        if (feedCache.isNotEmpty()) localRepository.storeItems(feedCache)

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

        savedCache = SOURCES.firstListResultOrNull { it.getSavedInsertionList(cached) }
                ?: emptyList()

        if (savedCache.isNotEmpty()) localRepository.storeItems(savedCache)

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

        publishedCache = SOURCES.firstListResultOrNull { it.getPublishedInsertionList(cached) }
                ?: emptyList()

        if (publishedCache.isNotEmpty()) localRepository.storeItems(publishedCache)

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
        val savedOnServer = serverRepository.toggleInsertionSaveStatus(insertionId)
        if (savedOnServer) {
            localRepository.toggleInsertionSaveStatus(insertionId)
        }
        return savedOnServer
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishInsertion(insertion: Insertion) : Boolean {
        val publishedOnServer = serverRepository.publishInsertion(insertion)
        if (publishedOnServer) {
            localRepository.publishInsertion(insertion)
        }
        return publishedOnServer
    }
}