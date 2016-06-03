package com.nibokapp.nibok.data.repository

import com.nibokapp.nibok.domain.model.BookModel

object BookManager {

    val books = mutableListOf<BookModel>()

    init {
        books.addAll(genMockBooks())
    }

    fun getBooksList() : List<BookModel> = books

    fun getNewerBooks(): List<BookModel> {
        val newBooks = genMockBooks(end = 3, title = "Newer title")
        books.addAll(0, newBooks)
        return newBooks
    }

    fun getOlderBooks(): List<BookModel> {
        val oldBooks = genMockBooks(end = 4, title = "Older title")
        books.addAll(oldBooks)
        return oldBooks
    }

    fun hasNewerBooks() = true

    fun hasOlderBooks() = true

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