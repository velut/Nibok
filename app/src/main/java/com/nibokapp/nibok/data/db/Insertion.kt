package com.nibokapp.nibok.data.db

import com.nibokapp.nibok.data.db.common.RealmString
import com.nibokapp.nibok.data.db.common.WellFormedItem
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
 * @param bookPictures the sources of the pictures of the book taken by the user
 */
open class Insertion(

        @PrimaryKey open var id: String = "",

        open var date: Date? = null,

        open var seller: ExternalUser? = null,

        open var book: Book? = null,

        open var bookPrice: Float = 0f,

        open var bookCondition: String = "",

        open var bookPictures: RealmList<RealmString> = RealmList()

) : RealmObject(), WellFormedItem {

    override fun isWellFormed(): Boolean = with(this) {
        date != null && seller != null && book != null
    }
}
