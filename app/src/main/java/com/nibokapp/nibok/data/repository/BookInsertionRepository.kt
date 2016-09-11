package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.data.repository.common.BookInsertionRepositoryInterface
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.queryRealm
import com.nibokapp.nibok.extension.toNormalList
import com.nibokapp.nibok.extension.withRealm
import io.realm.Case
import io.realm.Realm
import java.util.*

/**
 * Repository for book insertions.
 */
object BookInsertionRepository : BookInsertionRepositoryInterface {

    const private val TAG = "BookInsertionRepository"

    /*
     * COMMON FUNCTIONS
     */

    override fun getBookInsertionById(insertionId: Long) : Insertion? = queryOneWithRealm {
        it.where(Insertion::class.java)
                .equalTo("id", insertionId)
                .findFirst()
    }

    override fun getBookInsertionListFromQuery(query: String) : List<Insertion> {

        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) return emptyList()

        val results = queryRealm {
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

    override fun getBookInsertionListAfterDate(date: Date) : List<Insertion> {
        val results = queryRealm {
            it.where(Insertion::class.java)
                    .greaterThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    override fun getBookInsertionListBeforeDate(date: Date) : List<Insertion> {
        val results = queryRealm {
            it.where(Insertion::class.java)
                    .lessThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} insertions before $date")
        return results
    }

    /*
     * FEED BOOK INSERTIONS
     */

    override fun getFeedBookInsertionList(): List<Insertion> {
        val results = queryRealm { it.where(Insertion::class.java).findAll()}
                .excludeUserOwnInsertions()
        Log.d(TAG, "Found ${results.size} feed insertions")
        return results
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

    override fun getSavedBookInsertionList() : List<Insertion> =
            UserRepository.getLocalUser()?.savedInsertions?.toNormalList() ?: emptyList()

    override fun getSavedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlySavedInsertions()

    override fun getSavedBookInsertionLisAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlySavedInsertions()

    override fun getSavedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlySavedInsertions()

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    override fun getPublishedBookInsertionList() : List<Insertion> =
            UserRepository.getLocalUser()?.publishedInsertions?.toNormalList() ?: emptyList()

    override fun getPublishedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    override fun getPublishedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS
     */

    override fun isBookInsertionSaved(insertionId: Long) : Boolean =
            insertionId in getSavedBookInsertionList().map { it.id }

    override fun toggleBookInsertionSaveStatus(insertionId: Long) : Boolean {

        if (!UserRepository.localUserExists())
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
     * REPOSITORY EXTENSIONS
     */

    private fun Realm.getBookInsertionById(insertionId: Long) : Insertion? =
            this.where(Insertion::class.java).equalTo("id", insertionId).findFirst()

    private fun Realm.getLocalUser() : User? =
            this.where(User::class.java).findFirst()


    private fun List<Insertion>.excludeUserOwnInsertions() : List<Insertion> {
        if (!UserRepository.localUserExists()) {
            return this
        } else {
            val userId = UserRepository.getLocalUserId()
            return this.filter { it.seller?.id != userId }
        }
    }

    private fun List<Insertion>.includeOnlyUserOwnInsertions() : List<Insertion> {
        if (!UserRepository.localUserExists()) {
            return this
        } else {
            val userId = UserRepository.getLocalUserId()
            return this.filter { it.seller?.id == userId }
        }
    }

    private fun List<Insertion>.includeOnlySavedInsertions() : List<Insertion> {
        val savedInsertions = getSavedBookInsertionList()
        return this.filter { it in savedInsertions }
    }
}