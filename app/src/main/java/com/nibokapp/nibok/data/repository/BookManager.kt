package com.nibokapp.nibok.data.repository

import com.nibokapp.nibok.domain.model.BookModel

object BookManager {

    val books = mutableListOf<BookModel>()

    init {
        // Mock books
        for (i in 1..10) {
            books.add(
                    BookModel(
                            "Title is $i",
                            "Author num $i",
                            2000 + i,
                            "Light wear",
                            10 + i,
                            10 + i,
                            "http://lorempixel.com/300/400/abstract/$i"
                    )
            )
        }
    }

    fun getLatestBooks() : List<BookModel> = books
}