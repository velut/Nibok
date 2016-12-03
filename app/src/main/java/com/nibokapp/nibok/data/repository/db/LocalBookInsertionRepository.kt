package com.nibokapp.nibok.data.repository.db

import android.util.Log
import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.db.common.LocalStorage
import com.nibokapp.nibok.extension.*
import io.realm.Case
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * Local repository for book insertions.
 */
object LocalBookInsertionRepository :
        BookInsertionRepositoryInterface,
        LocalStorage<Insertion> {

    const private val TAG = "LocalBookInsertionRepo"

    private val userRepository: UserRepositoryInterface = UserRepository

    private var feedCache: List<Insertion> = emptyList()
    private var savedCache: List<Insertion> = emptyList()
    private var publishedCache: List<Insertion> = emptyList()

    /*
     * COMMON FUNCTIONS
     */

    override fun getInsertionById(insertionId: String): Insertion? = queryOneRealm {
        it.where(Insertion::class.java)
                .equalTo("id", insertionId)
                .findFirst()
    }

    override fun getBookByISBN(isbn: String): Book? = queryOneRealm {
        it.where(Book::class.java)
                .equalTo("isbn", isbn)
                .findFirst()
    }

    override fun getInsertionListFromQuery(query: String): List<Insertion> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = queryManyRealm {
            it.where(Insertion::class.java)
                    .contains("book.title", trimmedQuery, Case.INSENSITIVE)
                    .or()
                    .contains("book.authors.value", trimmedQuery, Case.INSENSITIVE)
                    .or()
                    .contains("book.publisher", trimmedQuery, Case.INSENSITIVE)
                    .or()
                    .contains("book.isbn", trimmedQuery, Case.INSENSITIVE)
                    .findAll()
        }
        Log.d(TAG, "Book insertions corresponding to query '$query' = ${results.size}")
        return results
    }

    override fun getInsertionListAfterDate(date: Date): List<Insertion> {
        val results = queryManyRealm {
            it.where(Insertion::class.java)
                    .greaterThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    override fun getInsertionListBeforeDate(date: Date): List<Insertion> {
        val results = queryManyRealm {
            it.where(Insertion::class.java)
                    .lessThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} insertions before $date")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return feedCache
        feedCache = queryManyRealm { it.where(Insertion::class.java).findAll()}
                .excludeUserOwnInsertions()
        Log.d(TAG, "Found ${feedCache.size} feed insertions")
        return feedCache
    }

    override fun getFeedInsertionListFromQuery(query: String): List<Insertion>  =
            getInsertionListFromQuery(query).excludeUserOwnInsertions()

    override fun getFeedInsertionListAfterDate(date: Date): List<Insertion> =
            getInsertionListAfterDate(date).excludeUserOwnInsertions()

    override fun getFeedInsertionListBeforeDate(date: Date): List<Insertion> =
            getInsertionListBeforeDate(date).excludeUserOwnInsertions()

    /*
     * SAVED BOOK INSERTIONS
     */

    override fun getSavedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return savedCache
        savedCache = userRepository.getLocalUser()?.savedInsertions?.toNormalList() ?: emptyList()
        return savedCache
    }

    override fun getSavedInsertionListFromQuery(query: String): List<Insertion> =
            getInsertionListFromQuery(query).includeOnlySavedInsertions()

    override fun getSavedInsertionLisAfterDate(date: Date): List<Insertion> =
            getInsertionListAfterDate(date).includeOnlySavedInsertions()

    override fun getSavedInsertionListBeforeDate(date: Date): List<Insertion> =
            getInsertionListBeforeDate(date).includeOnlySavedInsertions()

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedInsertionList(cached: Boolean): List<Insertion> {
        if (cached) return publishedCache
        publishedCache =
                userRepository.getLocalUser()?.publishedInsertions?.toNormalList() ?: emptyList()
        return publishedCache
    }

    override fun getPublishedInsertionListFromQuery(query: String): List<Insertion> =
            getInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    override fun getPublishedInsertionListAfterDate(date: Date): List<Insertion> =
            getInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    override fun getPublishedInsertionListBeforeDate(date: Date): List<Insertion> =
            getInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: String): Boolean =
            insertionId in getSavedInsertionList().map { it.id }

    override fun toggleInsertionSaveStatus(insertionId: String): Boolean {

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

    override fun publishInsertion(insertion: Insertion): Boolean =
            // TODO
            throw UnsupportedOperationException()

    /*
     * LOCAL STORAGE
     */
    override fun storeItems(items: List<Insertion>) {

        if (items.isEmpty()) return
        doAsync {
            Log.d(TAG, "Storing insertions")
            items.forEach { storeInsertion(it) }
        }
    }

    private fun storeInsertion(insertion: Insertion) {
        Log.d(TAG, "Storing insertion: ${insertion.id}")
        executeRealmTransaction {
            it.copyToRealmOrUpdate(insertion)
        }
    }
}
