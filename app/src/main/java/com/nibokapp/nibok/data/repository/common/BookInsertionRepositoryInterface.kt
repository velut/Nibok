package com.nibokapp.nibok.data.repository.common

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import java.util.*

/**
 * Interface for book insertions repositories.
 */
interface BookInsertionRepositoryInterface {

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
    fun getBookInsertionById(insertionId: String) : Insertion?

    /**
     * Get the book data for the book with the given ISBN code.
     *
     * @param isbn the ISBN code of the book
     *
     * @return book data about the book with the given ISBN code if the book was found, null otherwise
     */
    fun getBookByISBN(isbn: String) : Book?

    /**
     * Get the list of insertions matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getBookInsertionListFromQuery(query: String) : List<Insertion>

    /**
     * Get the list of insertions with a date greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getBookInsertionListAfterDate(date: Date) : List<Insertion>

    /**
     * Get the list of insertions with a date smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getBookInsertionListBeforeDate(date: Date) : List<Insertion>

    /*
     * FEED BOOK INSERTIONS
     */

    /**
     * Get the current list of book insertions to display in a feed.
     *
     * Insertions published by the local user are excluded from this list.
     *
     * @param cached set to true to return cached data. Default is false
     *
     * @return the list of currently available book insertions published by external users
     */
    fun getFeedBookInsertionList(cached: Boolean = false): List<Insertion>

    /**
     * Get the list of insertions not published by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getFeedBookInsertionListFromQuery(query: String) : List<Insertion>

    /**
     * Get the list of insertions not published by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getFeedBookInsertionListAfterDate(date: Date) : List<Insertion>

    /**
     * Get the list of insertions not published by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getFeedBookInsertionListBeforeDate(date: Date) : List<Insertion>

    /*
     * SAVED BOOK INSERTIONS
     */

    /**
     * Get the list of insertions saved by the user if such list is available.
     *
     * @param cached set to true to return cached data. Default is false
     *
     * @return the list of insertions saved by the user
     * or an empty list if no saved insertions could be found
     */
    fun getSavedBookInsertionList(cached: Boolean = false) : List<Insertion>

    /**
     * Get the list of insertions saved by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getSavedBookInsertionListFromQuery(query: String) : List<Insertion>

    /**
     * Get the list of insertions saved by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getSavedBookInsertionLisAfterDate(date: Date) : List<Insertion>

    /**
     * Get the list of insertions saved by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getSavedBookInsertionListBeforeDate(date: Date) : List<Insertion>

    /*
     * PUBLISHED BOOK INSERTIONS
     */

    /**
     * Get the list of insertions published by the user if such list is available.
     *
     * @param cached set to true to return cached data. Default is false
     *
     * @return the list of insertions published by the user
     * or an empty list if no published insertions could be found
     */
    fun getPublishedBookInsertionList(cached: Boolean = false) : List<Insertion>

    /**
     * Get the list of insertions published by the user matching the given query.
     * The query can match on the following book attributes:
     *  Title, Authors, Publisher, ISBN code
     *
     *  @param query the string describing the query
     *
     *  @return the list of book insertions matching the query
     */
    fun getPublishedBookInsertionListFromQuery(query: String) : List<Insertion>

    /**
     * Get the list of insertions published by the user with a date
     * greater or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date greater or equal to the given date
     */
    fun getPublishedBookInsertionListAfterDate(date: Date) : List<Insertion>

    /**
     * Get the list of insertions published by the user with a date
     * smaller or equal to the given date.
     *
     * @param date the date used in comparisons
     *
     *  @return the list of book insertions with a date smaller or equal to the given date
     */
    fun getPublishedBookInsertionListBeforeDate(date: Date) : List<Insertion>

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
    fun isBookInsertionSaved(insertionId: String) : Boolean

    /**
     * Toggle the save status for an insertion either by adding it to the user saved insertions
     * if it was not already saved or by removing it if it was already saved.
     *
     * @param insertionId the id of the insertion to add or remove
     *
     * @return true if the insertion was saved false if it was removed
     */
    fun toggleBookInsertionSaveStatus(insertionId: String) : Boolean

    /*
     * BOOK INSERTION PUBLISHING
     */

    /**
     * Publish the given book insertion.
     *
     * @param insertion the insertion to publish
     *
     * @return true if the book insertion was published successfully, false otherwise
     */
    fun publishBookInsertion(insertion: Insertion) : Boolean

}