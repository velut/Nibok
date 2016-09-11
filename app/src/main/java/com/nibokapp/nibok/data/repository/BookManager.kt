package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.domain.mapper.BookInsertionDataMapper
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.queryRealm
import io.realm.Case

/**
 * Singleton that manages the retrieval of books published on the platform.
 */
object BookManager {

    const val TAG = "BookManager"


    /**
     * Get detailed data about a given insertion.
     *
     * @param insertionId the id of the insertion
     *
     * @return a BookInsertionModel instance containing detailed data about the insertion
     */
    fun getInsertionDetails(insertionId: Long) : BookInsertionModel? {
        val insertion = queryOneWithRealm {
                            it.where(Insertion::class.java)
                            .equalTo("id", insertionId)
                            .findFirst()
        }
        var bookInsertion: BookInsertionModel? = null
        insertion?.let {
            bookInsertion = BookInsertionDataMapper().convertInsertionToDomain(it)
        }
        return bookInsertion
    }

    /**
     * Get the list of books where the title, authors, publisher or isbn contain the given query.
     *
     * @param query the query
     *
     * @return a list of BookInsertionModel instances with data about the found books
     */
    fun getBooksFromQuery(query: String) : List<BookInsertionModel> {

        // Remove leading and trailing whitespaces
        val trimmedQuery = query.trim()

        if (trimmedQuery.isEmpty()) {
            return emptyList()
        }

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
        Log.d(TAG, "Books corresponding to query '$query' = ${results.size}")
        val booksList = BookInsertionDataMapper().convertInsertionListToDomain(results)
        Log.d(TAG, "Books list size: ${booksList.size}")
        return booksList
    }

    /**
     * Get the current book list to display in the feed.
     *
     * @return the list of currently available book for the feed
     */
    fun getFeedBooksList() : List<BookInsertionModel> {
        val results = queryRealm { it.where(Insertion::class.java).findAll() }
        Log.d(TAG, "Found ${results.size} feed items")
        return BookInsertionDataMapper().convertInsertionListToDomain(results)
    }

    /**
     * Get the current list of saved books.
     *
     * @return the list of saved books
     */
    fun getSavedBooksList(): List<BookInsertionModel> =
            BookInsertionDataMapper().convertInsertionListToDomain(UserManager.getUserSavedInsertions())

    /**
     * Signals if newer books are available.
     *
     * @return true if newer books are available, false otherwise
     */
    fun hasNewerFeedBooks() = false

    /**
     * Signals if older books are available.
     *
     * @return true if older books are available, false otherwise
     */
    fun hasOlderFeedBooks() = false

    /**
     * Signals if older saved books are available.
     *
     * @return true if older saved books are available, false otherwise
     */
    fun  hasOlderSavedBooks() = false

}