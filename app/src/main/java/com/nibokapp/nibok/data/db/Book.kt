package com.nibokapp.nibok.data.db

import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Model class for the books.
 * Used by the local database.
 *
 * @param title the title of the book
 * @param authors the list of authors of the book
 * @param year the year in which the book was published
 * @param publisher the publisher of the book
 * @param isbn the isbn of the book
 */
open class Book(

        open var title: String = "",

        open var authors: RealmList<RealmString> = RealmList(),

        open var year: Int = 0,

        open var publisher: String = "",

        open var isbn: String = ""

) : RealmObject() {}
