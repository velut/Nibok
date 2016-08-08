package com.nibokapp.nibok.ui

import android.app.Application
import com.nibokapp.nibok.domain.model.Book
import com.nibokapp.nibok.domain.model.Insertion
import com.nibokapp.nibok.domain.model.Seller
import com.nibokapp.nibok.domain.model.common.RealmString
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val realmConfig = RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded() // Delete the DB instead of migrating
                .build()

        Realm.deleteRealm(realmConfig) // Delete realm on restart

        Realm.setDefaultConfiguration(realmConfig)

        val realm = Realm.getDefaultInstance()


        // For testing
        realm.executeTransaction {
            val seller = realm.createObject(Seller::class.java)
            seller.name = "Test2"
            seller.id = 2

            val insertion = realm.createObject(Insertion::class.java)
            val book = realm.createObject(Book::class.java)

            val a1 = realm.createObject(RealmString::class.java)
            val a2 = realm.createObject(RealmString::class.java)
            val a3 = realm.createObject(RealmString::class.java)
            a1.value = "John Doe"
            a2.value = "Bob Zu"
            a3.value = "example.com"

            book.id = 1
            book.title = "Title"
            book.authors?.add(a1)
            book.authors?.add(a2)
            book.year = 2016
            book.publisher = "Mit Press"
            book.isbn = "1234"

            insertion.id = 1
            insertion.date = Date()
            insertion.seller = seller
            insertion.book = book
            insertion.bookPrice = 99.99f
            insertion.bookCondition = "Light wear"
            insertion.bookImagesSources?.add(a3)

        }
    }
}