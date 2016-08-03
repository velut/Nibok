package com.nibokapp.nibok.data.repository

import com.nibokapp.nibok.domain.model.BookModel

/**
 * Singleton that manages the retrieval of books published on the platform.
 */
object BookManager {

    // The list of books
    val books = mutableListOf<BookModel>()

    init {
        books.addAll(genMockBooks())
    }

    /**
     * Get the current book list.
     *
     * @return the list of currently available book
     */
    fun getBooksList() : List<BookModel> = books

    /**
     * Get newer books published since the last retrieval and add them to the books list.
     *
     * @return list of newer books published since the last retrieval
     */
    fun getNewerBooks(): List<BookModel> {
        val newBooks = genMockBooks(end = 3, title = "Newer title")
        books.addAll(0, newBooks)
        return newBooks
    }

    /**
     * Get older books published before the last one currently displayed
     * and add them to the books list.
     *
     * @return list of older books published before the last one currently displayed
     */
    fun getOlderBooks(): List<BookModel> {
        val oldBooks = genMockBooks(end = 4, title = "Older title")
        books.addAll(oldBooks)
        return oldBooks
    }

    /**
     * Get older books saved by the user and add them to the saved book list.
     *
     * @return list of older books saved by the user
     */
    fun  getOlderSavedBooks(): List<BookModel> {
        val oldBooks = genMockBooks(end = 4, title = "Older Saved title")
        books.addAll(oldBooks)
        return oldBooks
    }

    /**
     * Signals if newer books are available.
     *
     * @return true if newer books are available, false otherwise
     */
    fun hasNewerBooks() = true

    /**
     * Signals if older books are available.
     *
     * @return true if older books are available, false otherwise
     */
    fun hasOlderBooks() = true

    /**
     * Signals if older saved books are available.
     *
     * @return true if older saved books are available, false otherwise
     */
    fun  hasOlderSavedBooks() = true

    // Temporary. Generate mocked books
    private fun genMockBooks(start: Int = 1, end: Int = 10, title: String = "Title"): List<BookModel> {
        val books = mutableListOf<BookModel>()
        for (i in start..end) {
            books.add(
                    BookModel(
                            "$title is $i",
                            "Author num $i",
                            2000 + i,
                            "Light wear",
                            10 + i,
                            10 + i,
                            "http://lorempixel.com/300/400/abstract/$i"
                    )
            )
        }
        return books
    }
}