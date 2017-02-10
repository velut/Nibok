package com.nibokapp.nibok.data.db

import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for the local user.
 * Used by the local database.
 *
 * @param username the username of the user. Primary key
 * @param savedInsertionsIds the insertions saved by the user - One to many
 */
open class User(

        @PrimaryKey open var username: String = "",

        open var savedInsertionsIds: RealmList<RealmString> = RealmList()

) : RealmObject()
