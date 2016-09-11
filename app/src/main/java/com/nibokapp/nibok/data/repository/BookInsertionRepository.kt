package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
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
object BookInsertionRepository {

    const private val TAG = "BookInsertionRepository"

    /*
     * COMMON FUNCTIONS
     */

    /**
     * Get the insertion with the given id.
     *
     * @param insertionId the id of the insertion
     *
     * @return the insertion with the given id or null if no such insertion was found
     */
    fun getBookInsertionById(insertionId: Long) : Insertion? = queryOneWithRealm {
        it.where(Insertion::class.java)
                .equalTo("id", insertionId)
                .findFirst()
    }

    /**
     * Get the list of insertions matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getBookInsertionListFromQuery(query: String) : List<Insertion> {

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

    /**
     * Get the list of insertions with a date greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getBookInsertionListAfterDate(date: Date) : List<Insertion> {
        val results = queryRealm {
            it.where(Insertion::class.java)
                    .greaterThanOrEqualTo("date", date)
                    .findAll()}
        Log.d(TAG, "Found ${results.size} insertions after $date")
        return results
    }

    /**
     * Get the list of insertions with a date smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getBookInsertionListBeforeDate(date: Date) : List<Insertion> {
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

    /**
     * Get the current list of book insertions to display in a feed.
     *
     * Insertions published by the local user are excluded from this list.
     *
     * @return the list of currently available book insertions published by external users
     */
    fun getFeedBookInsertionList(): List<Insertion> {
        val results = queryRealm { it.where(Insertion::class.java).findAll()}
                .excludeUserOwnInsertions()
        Log.d(TAG, "Found ${results.size} feed insertions")
        return results
    }

    /**
     * Get the list of insertions not published by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getFeedBookInsertionListFromQuery(query: String) : List<Insertion>  =
            getBookInsertionListFromQuery(query).excludeUserOwnInsertions()

    /**
     * Get the list of insertions not published by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getFeedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).excludeUserOwnInsertions()

    /**
     * Get the list of insertions not published by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getFeedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).excludeUserOwnInsertions()

    /*
     * SAVED BOOK INSERTIONS
     */

    /**
     * Get the list of insertions saved by the user if such list is available.
     *
     * @return the list of insertions saved by the user
     * or an empty list if no saved insertions could be found
     */
    fun getSavedBookInsertionList() : List<Insertion> =
            UserRepository.getLocalUser()?.savedInsertions?.toNormalList() ?: emptyList()

    /**
     * Get the list of insertions saved by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getSavedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlySavedInsertions()

    /**
     * Get the list of insertions saved by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getSavedBookInsertionLisAftereDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlySavedInsertions()

    /**
     * Get the list of insertions saved by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getSavedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlySavedInsertions()

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    /**
     * Get the list of insertions published by the user if such list is available.
     *
     * @return the list of insertions published by the user
     * or an empty list if no published insertions could be found
     */
    fun getPublishedBookInsertionList() : List<Insertion> =
            UserRepository.getLocalUser()?.publishedInsertions?.toNormalList() ?: emptyList()

    /**
     * Get the list of insertions published by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getPublishedBookInsertionListFromQuery(query: String) : List<Insertion> =
            getBookInsertionListFromQuery(query).includeOnlyUserOwnInsertions()

    /**
     * Get the list of insertions published by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getPublishedBookInsertionListAfterDate(date: Date) : List<Insertion> =
            getBookInsertionListAfterDate(date).includeOnlyUserOwnInsertions()

    /**
     * Get the list of insertions published by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getPublishedBookInsertionListBeforeDate(date: Date) : List<Insertion> =
            getBookInsertionListBeforeDate(date).includeOnlyUserOwnInsertions()

    /*
     * BOOK INSERTION SAVE STATUS
     */

    /**
     * Check if the book insertion with the given id is among the ones saved by the user or not.
     *
     * @param insertionId the id of the book insertion
     *
     * @return true if the book insertion belongs to the user's saved insertions, false otherwise
     */
    fun isBookInsertionSaved(insertionId: Long) : Boolean =
            insertionId in getSavedBookInsertionList().map { it.id }

    /**
     * Toggle the save status for an insertion either by adding it to the user saved insertions
     * if it was not already saved or by removing it if it was already saved.
     *
     * @param insertionId the id of the insertion to add or remove
     *
     * @return true if the insertion was saved false if it was removed
     */
    fun toggleBookInsertionSaveStatus(insertionId: Long) : Boolean {

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