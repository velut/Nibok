package com.nibokapp.nibok.domain.model

import io.realm.RealmObject

/**
 * String wrapper for string lists in the local DB.
 *
 * @property value the value of the string
 */
open class RealmString(
        open var value: String = ""
) : RealmObject () {}
