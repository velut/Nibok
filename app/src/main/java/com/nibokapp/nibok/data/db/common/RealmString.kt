package com.nibokapp.nibok.data.db.common

import io.realm.RealmObject

/**
 * String wrapper for string lists in the local DB.
 *
 * @param value the value of the string
 */
open class RealmString(
        open var value: String = ""
) : RealmObject() {}
