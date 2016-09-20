package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.db.LocalBookInsertionRepository
import com.nibokapp.nibok.data.repository.server.ServerBookInsertionRepository
import com.nibokapp.nibok.extension.*
import java.util.*

/**
 * Repository for book insertions.
 */
object BookInsertionRepository : BookInsertionRepositoryInterface {

    const private val TAG = "BookInsertionRepository"

    private val userRepository : UserRepositoryInterface = UserRepository

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

        val results = SOURCES.firstResultOrNull { it.getBookInsertionListFromQuery(query) }
                ?: emptyList()

        Log.d(TAG, "Book insertions corresponding to query '$query' = ${results.size}")

        return results
    }

    override fun getBookInsertionListAfterDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstResultOrNull { it.getBookInsertionListAfterDate(date) }
                ?: emptyList()
        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    override fun getBookInsertionListBeforeDate(date: Date) : List<Insertion> {
        val results = SOURCES.firstResultOrNull { it.getBookInsertionListBeforeDate(date) }
                ?: emptyList()
        Log.d(TAG, "Found ${results.size} insertions before $date")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedBookInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        feedCache = SOURCES.firstResultOrNull { it.getFeedBookInsertionList(cached) }
                ?: emptyList()
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
        savedCache = SOURCES.firstResultOrNull { it.getSavedBookInsertionList(cached) }
                ?: emptyList()
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
        publishedCache = SOURCES.firstResultOrNull { it.getPublishedBookInsertionList(cached) }
                ?: emptyList()
        return publishedCache
    }

    override fun getPublishedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS TODO
     */

    override fun isBookInsertionSaved(insertionId: String) : Boolean =
            insertionId in getSavedBookInsertionList().map { it.id }

    override fun toggleBookInsertionSaveStatus(insertionId: String) : Boolean {

        if (!userRepository.localUserExists())
            throw IllegalStateException("Local user does not exist. Cannot save insertion")

        var saved = false

        withRealm {
            val insertion = it.getBookInsertionById(insertionId)
            val user = it.getLocalUser()
            val savedInsertions = user!!.savedInsertions

            saved = insertion in savedInsertions

            it.executeTransaction {
                if (!saved) {
                    savedInsertions.add(0, insertion)
                } else {
                    savedInsertions.remove(insertion)
                }
            }
            saved = insertion in savedInsertions
            Log.d(TAG, "After toggle: Save status: $saved, saved size: ${savedInsertions.size}")
        }
        return saved
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishBookInsertion(insertion: Insertion) : Boolean =
            // TODO
            throw UnsupportedOperationException()
}