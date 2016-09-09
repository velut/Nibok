package com.nibokapp.nibok.data.db

import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Model class for the books insertions.
 * Used by the local database.
 *
 * @param id the id of the insertion - Primary Key
 * @param date the date of the insertion
 * @param seller the user that created the insertion - One to one
 * @param book the book being sold in this insertion - One to one
 * @param bookPrice the price the user set for the book
 * @param bookCondition the wear condition of the book
 * @param bookImagesSources the sources of the pictures of the book taken by the user
 */
open class Insertion(

        @PrimaryKey open var id: Long = 0,

        open var date: Date? = null,

        open var seller: Seller? = null,

        open var book: Book? = null,

        open var bookPrice: Float = 0f,

        open var bookCondition: String = "",

        open var bookImagesSources: RealmList<RealmString> = RealmList()

) : RealmObject() {}
