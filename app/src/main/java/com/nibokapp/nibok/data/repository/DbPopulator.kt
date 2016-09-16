package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.*
import com.nibokapp.nibok.data.db.common.RealmString
import com.nibokapp.nibok.extension.withRealm
import io.realm.Realm
import io.realm.RealmList
import java.util.*

/**
 * Test class used to populate the db.
 */
class DbPopulator {

    companion object {
        private val TAG = DbPopulator::class.java.simpleName
    }

    /**
     * Add some data to the local db.
     */
    fun populateDb() {
        Log.d(TAG, "Populating DB with test data")
        withRealm {
            it.executeTransaction {
                for (i in 1..10) {
                    val authors = listOf(genAuthor(it,"John Doe $i"), genAuthor(it, "Bob Zu $i"))
                    val book = genBook(it, "Title $i", authors, 2016, "Mit Press", "$i")

                    val thumbnail = genThumbnail(it)
                    val seller = genSeller(it, i.toLong(), "Tom Seller $i")

                    val insertion = genInsertion(it, i.toLong(), seller, book, 10.50f, "Light wear", thumbnail)

                    if (i == 1) {
                        it.getLocalUser()!!.publishedInsertions.add(insertion)
                    }

                    genConversation(it, i.toLong(), seller)
                }
            }
        }
    }

    private fun genConversation(realm: Realm, convid: Long, cpart: ExternalUser) {
        val conv = realm.createObject(Conversation::class.java)
        conv.apply {
            id = convid
            userId = 1
            partner = cpart
            date = Date()
            messages = RealmList(realm.copyToRealm(Message(convid,cpart.id,"Hello",Date())))
        }
        realm.getLocalUser()?.conversations?.add(conv)
    }

    private fun Realm.getLocalUser() : User? =
            this.where(User::class.java).findFirst()

    fun populateDb2() {
        Log.d(TAG, "Populating DB with test data")
        withRealm {
            it.executeTransaction {
                val i = Random().nextInt()
                val authors = listOf(genAuthor(it,"John Doe $i"), genAuthor(it, "Bob Zu $i"))
                val book = genBook(it, "Title $i", authors, 2016, "Mit Press", "$i")

                val thumbnail = genThumbnail(it)
                val seller = genSeller(it, i.toLong(), "Tom Seller $i")

                genInsertion(it, i.toLong(), seller, book, 10.50f, "Light wear", thumbnail)
            }
        }
    }

    private fun genSeller(realm: Realm, id: Long, name: String) : ExternalUser {
        val seller = realm.createObject(ExternalUser::class.java)
        seller.id = id
        seller.name = name
        seller.avatar = ""
        return seller
    }

    private fun genAuthor(realm: Realm, name: String) : RealmString {
        val author = realm.createObject(RealmString::class.java)
        author.value = name
        return author
    }

    private fun genThumbnail(realm: Realm, url: String = "https://placehold.it/300x400") : RealmString {
        val thumbnail = realm.createObject(RealmString::class.java)
        thumbnail.value = url
        return thumbnail
    }

    private fun genBook(realm: Realm, title: String, authors: List<RealmString>,
                        year: Int, publisher: String, isbn: String) : Book {
        val book = realm.createObject(Book::class.java)
        book.title = title
        book.authors.addAll(authors)
        book.year = year
        book.publisher = publisher
        book.isbn = isbn
        return book
    }

    private fun genInsertion(realm: Realm, id: Long, seller: ExternalUser,
                             book: Book, price: Float, condition: String,
                             thumbnail: RealmString, date: Date = Date(1000)) : Insertion {
        val insertion = realm.createObject(Insertion::class.java)
        insertion.id = id
        insertion.seller = seller
        insertion.book = book
        insertion.bookPrice = price
        insertion.bookCondition = condition
        insertion.bookImagesSources.add(thumbnail)
        insertion.date = date
        return insertion
    }
}
