package com.nibokapp.nibok.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for external users.
 * Used by the local database.
 *
 * @param id the id of the external user
 * @param name the display name of the external user
 * @param avatar the avatar of the external user
 */
open class ExternalUser(

        @PrimaryKey open var id: Long = 0,

        open var name: String = "",

        open var avatar: String = ""

) : RealmObject() {}
