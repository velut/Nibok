package com.nibokapp.nibok.domain.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for the books.
 * Used by the local database.
 *
 * @property id the id of the book - Primary Key
 * @property authors the list of authors of the book
 * @property year the year in which the book was published
 * @property publisher the publisher of the book
 * @property isbn the isbn of the book
 */
open class Book(

        @PrimaryKey open var id: Long = 0,

        open var title: String = "",

        open var authors: RealmList<RealmString>? = null,

        open var year: Int = 0,

        open var publisher: String = "",

        open var isbn: String = ""

) : RealmObject() {}
