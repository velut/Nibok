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
 * @property id the id of the insertion - Primary Key
 * @property date the date of the insertion
 * @property seller the user that created the insertion - One to one
 * @property book the book being sold in this insertion - One to one
 * @property bookPrice the price the user set for the book
 * @property bookCondition the wear condition of the book
 * @property bookImagesSources the sources of the pictures of the book taken by the user
 */
open class Insertion(

        @PrimaryKey open var id: Long = 0,

        open var date: Date? = null,

        open var seller: Seller? = null,

        open var book: Book? = null,

        open var bookPrice: Float = 0f,

        open var bookCondition: String = "",

        open var bookImagesSources: RealmList<RealmString>? = null

) : RealmObject() {}
