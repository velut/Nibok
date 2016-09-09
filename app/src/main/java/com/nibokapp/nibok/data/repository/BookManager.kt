package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.mapper.DbDataMapper
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.queryRealm
import io.realm.Case

/**
 * Singleton that manages the retrieval of books published on the platform.
 */
object BookManager {

    const val TAG = "BookManager"

    // The list of books in the feed
    val feedBooks = mutableListOf<BookModel>()

    // The list of saved books
    val savedBooks = mutableListOf<BookModel>()

    init {
        feedBooks.addAll(genMockBooks())
    }

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
            bookInsertion = DbDataMapper().convertInsertionToDetailDomain(it)
        }
        return bookInsertion
    }

    /**
     * Get the list of books where the title, authors, publisher or isbn contain the given query.
     *
     * @param query the query
     *
     * @return a list of BookModel instances with data about the found books
     */
    fun getBooksFromQuery(query: String) : List<BookModel> {

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
        val booksList = DbDataMapper().convertInsertionListToBookDomain(results)
        Log.d(TAG, "BookModel list size: ${booksList.size}")
        return booksList
    }

    /**
     * Get the current book list to display in the feed.
     *
     * @return the list of currently available book for the feed
     */
    fun getFeedBooksList() : List<BookModel> {
        val results = queryRealm { it.where(Insertion::class.java).findAll() }
        Log.d(TAG, "Found ${results.size} feed items")
        return DbDataMapper().convertInsertionListToBookDomain(results)
    }

    /**
     * Get the current list of saved books.
     *
     * @return the list of saved books
     */
    fun getSavedBooksList(): List<BookModel> =
            DbDataMapper().convertInsertionListToBookDomain(UserManager.getUserSavedInsertions())

    /**
     * Get newer books published since the last retrieval and add them to the books list.
     *
     * @return list of newer books published since the last retrieval
     */
    fun getNewerFeedBooks(): List<BookModel> {
        val newBooks = genMockBooks(end = 3, title = "Newer title")
        feedBooks.addAll(0, newBooks)
        return newBooks
    }

    /**
     * Get older books published before the last one currently displayed
     * and add them to the books list.
     *
     * @return list of older books published before the last one currently displayed
     */
    fun getOlderFeedBooks(): List<BookModel> {
        val oldBooks = genMockBooks(end = 4, title = "Older title")
        feedBooks.addAll(oldBooks)
        return oldBooks
    }

    /**
     * Get older books saved by the user and add them to the saved book list.
     *
     * @return list of older books saved by the user
     */
    fun  getOlderSavedBooks(): List<BookModel> {
        val oldBooks = genMockBooks(end = 4, title = "Older Saved title")
        savedBooks.addAll(oldBooks)
        return oldBooks
    }

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

    // Temporary. Generate mocked books
    private fun genMockBooks(start: Int = 1, end: Int = 10, title: String = "Title"): List<BookModel> {
        val books = mutableListOf<BookModel>()
        for (i in start..end) {
            books.add(
                    BookModel(
                            0,
                            "$title is $i",
                            "Author num $i",
                            2000 + i,
                            "Light wear",
                            10f + i + (50 + i)/100f,
                            "http://lorempixel.com/300/400/abstract/$i"
                    )
            )
        }
        return books
    }
}