package com.nibokapp.nibok.data.repository.server

import android.util.Log
import com.baasbox.android.BaasDocument
import com.baasbox.android.BaasQuery
import com.baasbox.android.BaasUser
import com.baasbox.android.BaasUser.Scope
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.*
import java.util.*

/**
 * Server repository for book insertions.
 */
object ServerBookInsertionRepository: BookInsertionRepositoryInterface {

    const private val TAG = "ServerBookInsertionRepository"

    private val userRepository : UserRepositoryInterface = UserRepository

    private var feedCache : List<Insertion> = emptyList()
    private var savedCache : List<Insertion> = emptyList()
    private var publishedCache : List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getBookInsertionById(insertionId: String) : Insertion? {
        val result = BaasDocument.fetchSync(ServerConstants.COLLECTION_INSERTIONS, insertionId)
        if (result.isSuccess && result.value() != null) {
            val insertion = buildInsertionFromDocument(result.value())
            return insertion
        } else {
            return null
        }
    }

    override fun getBookByISBN(isbn: String): Book? {
        return fetchBookFromISBN(isbn)
    }

    override fun getBookInsertionListFromQuery(query: String) : List<Insertion> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val whereString =
                "${ServerConstants.TITLE} like $query or" +
                "$query in ${ServerConstants.AUTHORS} or" +
                "${ServerConstants.PUBLISHER} like $query or" +
                "${ServerConstants.ISBN} like $query"

        val serverQuery = BaasQuery.builder()
                .where(whereString)
                .criteria()

        val result = BaasDocument.fetchAllSync(ServerConstants.COLLECTION_INSERTIONS, serverQuery)
        if (result.isSuccess && result.value() != null) {
            val insertions = result.value().map { buildInsertionFromDocument(it) }
            Log.d(TAG, "Book insertions corresponding to query '$query' = ${insertions.size}")
            return insertions
        } else {
            return emptyList()
        }
    }

    override fun getBookInsertionListAfterDate(date: Date) : List<Insertion> {

        val whereString =
                "${ServerConstants.DATE} >= ${date.toStringDate()}"

        val serverQuery = BaasQuery.builder()
                .where(whereString)
                .criteria()

        val result = BaasDocument.fetchAllSync(ServerConstants.COLLECTION_INSERTIONS, serverQuery)
        if (result.isSuccess && result.value() != null) {
            val insertions = result.value().map { buildInsertionFromDocument(it) }
            Log.d(TAG, "Found ${insertions.size} insertions after $date")
            return insertions
        } else {
            return emptyList()
        }
    }

    override fun getBookInsertionListBeforeDate(date: Date) : List<Insertion> {
        val whereString =
                "${ServerConstants.DATE} <= ${date.toStringDate()}"

        val serverQuery = BaasQuery.builder()
                .where(whereString)
                .criteria()

        val result = BaasDocument.fetchAllSync(ServerConstants.COLLECTION_INSERTIONS, serverQuery)
        if (result.isSuccess && result.value() != null) {
            val insertions = result.value().map { buildInsertionFromDocument(it) }
            Log.d(TAG, "Found ${insertions.size} insertions before $date")
            return insertions
        } else {
            return emptyList()
        }
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedBookInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        val result = BaasDocument.fetchAllSync(ServerConstants.COLLECTION_INSERTIONS)
        if (result.isSuccess && result.value() != null) {
            feedCache = result.value().map { buildInsertionFromDocument(it) }
                    .excludeUserOwnInsertions()
            Log.d(TAG, "Found ${feedCache.size} feed insertions")
            return feedCache
        } else {
            return emptyList()
        }
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
        savedCache = BaasUser.current()?.getSavedInsertions() ?: emptyList()
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
        publishedCache = BaasUser.current()?.getPublishedInsertions() ?: emptyList()
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
        val user = BaasUser.current() ?:
                throw IllegalStateException("No user logged in. Cannot save insertion")

        user.getScope(Scope.PRIVATE)
                .getArray(ServerConstants.SAVED_INSERTIONS, JsonArray())
                .add(insertionId)

        val result = user.saveSync()
        val saved = result.isSuccess
        Log.d(TAG, "After toggle: Save status: $saved")
        return saved
    }

    /*
     * BOOK INSERTION PUBLISHING
     */

    override fun publishBookInsertion(insertion: Insertion) : Boolean =
            // TODO
            throw UnsupportedOperationException()


    private fun List<Insertion>.excludeUserOwnInsertions() : List<Insertion> {
        val user = BaasUser.current()
        if (user == null) {
            return this
        } else {
            val userId = user.name
            return this.filter { it.seller?.username != userId }
        }
    }

    private fun List<Insertion>.includeOnlyUserOwnInsertions() : List<Insertion> {
        val user = BaasUser.current()
        if (user == null) {
            return this
        } else {
            val userId = user.name
            return this.filter { it.seller?.username == userId }
        }
    }

    private fun List<Insertion>.includeOnlySavedInsertions() : List<Insertion> {
        val savedInsertions = getSavedBookInsertionList()
        return this.filter { it.id in savedInsertions.map { it.id } }
    }
}
