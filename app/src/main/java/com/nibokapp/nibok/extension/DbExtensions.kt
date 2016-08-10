package com.nibokapp.nibok.extension

import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.Realm
import io.realm.RealmList

/**
 * Extension functions related to the Realm db.
 *
 */

/**
 * Convert a RealmList of RealmString into a normal String list.
 *
 * @return a list of strings
 */
fun RealmList<RealmString>.toStringList() : List<String> =
        this.map { it.value }

/**
 * Perform a transaction with Realm.
 */
inline fun withRealm(transaction: (realm: Realm) -> Unit) {
    val realm = Realm.getDefaultInstance()
    transaction(realm)
    realm.close()
}