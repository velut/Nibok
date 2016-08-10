package com.nibokapp.nibok.data.repository

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.Seller
import com.nibokapp.nibok.data.db.common.RealmString
import com.nibokapp.nibok.extension.withRealm
import io.realm.Realm
import java.util.*

/**
 * Test class used to populate the db.
 */
class DbPopulator {

    /**
     * Add some data to the local db.
     */
    fun populateDb() {
        withRealm {
            it.executeTransaction {
                val authors = listOf(genAuthor(it,"John Doe"), genAuthor(it, "Bob Zu"))
                val book = genBook(it, 1, "Title", authors, 2016, "Mit Press", "1234")

                val thumbnail = genThumbnail(it)
                val seller = genSeller(it, 1, "Tom Seller")

                genInsertion(it, 1, seller, book, 10.50f, "Light wear", thumbnail)
            }
        }
    }

    private fun genSeller(realm: Realm, id: Long, name: String) : Seller {
        val seller = realm.createObject(Seller::class.java)
        seller.id = id
        seller.name = name
        return seller
    }

    private fun genAuthor(realm: Realm, name: String) : RealmString {
        val author = realm.createObject(RealmString::class.java)
        author.value = name
        return author
    }

    private fun genThumbnail(realm: Realm, url: String = "http://lorempixel.com/300/400/food/2/") : RealmString {
        val thumbnail = realm.createObject(RealmString::class.java)
        thumbnail.value = url
        return thumbnail
    }

    private fun genBook(realm: Realm, id: Long, title: String, authors: List<RealmString>,
                        year: Int, publisher: String, isbn: String) : Book {
        val book = realm.createObject(Book::class.java)
        book.id = id
        book.title = title
        book.authors?.addAll(authors)
        book.year = year
        book.publisher = publisher
        book.isbn = isbn
        return book
    }

    private fun genInsertion(realm: Realm, id: Long, seller: Seller,
                             book: Book, price: Float, condition: String,
                             thumbnail: RealmString, date: Date = Date()) : Insertion {
        val insertion = realm.createObject(Insertion::class.java)
        insertion.id = id
        insertion.seller = seller
        insertion.book = book
        insertion.bookPrice = price
        insertion.bookCondition = condition
        insertion.bookImagesSources?.add(thumbnail)
        insertion.date = date
        return insertion
    }
}
