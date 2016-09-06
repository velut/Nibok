package com.nibokapp.nibok.data.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for the local user.
 * Used by the local database.
 *
 * @param id the id of the user
 * @param savedInsertions the insertions saved by the user - One to many
 * @param publishedInsertions the insertions published by the user - One to many
 */
open class User(

        @PrimaryKey open var id: Long = 0,

        open var savedInsertions: RealmList<Insertion>? = null,

        open var publishedInsertions: RealmList<Insertion>? = null

) : RealmObject() {}
