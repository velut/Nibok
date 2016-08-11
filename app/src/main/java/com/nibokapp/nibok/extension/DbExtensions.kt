package com.nibokapp.nibok.extension

import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

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

/**
 * Perform a query with Realm and return the list of results.
 */
inline fun <T: RealmModel> queryRealm(query: (realm: Realm) -> RealmResults<T>) : List<T> {
    val realm = Realm.getDefaultInstance()
    val results = realm.copyFromRealm(query(realm))
    realm.close()
    return results
}

/**
 * Perform a query with Realm and return a single result.
 */
inline fun <T: RealmModel> queryOneWithRealm(query: (realm: Realm) -> T) : T {
    val realm = Realm.getDefaultInstance()
    val realmResult = query(realm)
    val result = realm.copyFromRealm(realmResult)
    realm.close()
    return result
}