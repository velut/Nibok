package com.nibokapp.nibok.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for external users.
 * Used by the local database.
 *
 * @param username the username of the external user. Primary key
 * @param avatar the avatar of the external user
 */
open class ExternalUser(

        @PrimaryKey open var username: String = "",

        open var avatar: String = ""

) : RealmObject() {}
