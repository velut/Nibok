package com.nibokapp.nibok.extension

import android.util.Log
import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

val QUERY_TAG = "DB Query"
val CLOSE_REALM_MSG = "Closing realm after query"
val VERBOSE = false

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
 * Convert a List of String into a RealmList of RealmString.
 *
 * @return a a RealmList of RealmString
 */
fun List<String>.toRealmStringList() : RealmList<RealmString> {
    val realmList = RealmList<RealmString>()
    this.forEach { realmList.add(RealmString(it)) }
    return realmList
}

/**
 * Convert a RealmList of RealmModels into a normal list of RealmModel.
 *
 * @return a list of RealmModel
 */
fun <T: RealmModel> RealmList<T>.toNormalList() : List<T> =
        this.map { it }

/**
 * Execute the function with a realm as the parameter.
 *
 * @param func the function to execute with realm
 */
inline fun withRealm(func: (realm: Realm) -> Unit) {
    val realm = Realm.getDefaultInstance()
    func(realm)
    realm.close()
}

/**
 * Execute a realm transaction.
 *
 * @param transaction the transaction to be executed with realm
 */
inline fun executeRealmTransaction(crossinline transaction: (realm: Realm) -> Unit) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { transaction(realm) }
    realm.close()
}

/**
 * Perform a query with Realm and return the list of results.
 *
 * @return the list of RealmModel instances representing the found results or an empty list if no result was found
 */
inline fun <T: RealmModel> queryRealm(query: (realm: Realm) -> RealmResults<T>) : List<T> {
    val realm = Realm.getDefaultInstance()
    val realmResult = query(realm)
    var results: List<T> = emptyList()
    try {
        results = realm.copyFromRealm(realmResult)
    } catch (e: IllegalArgumentException) {
        Log.d(QUERY_TAG, "No list of objects found in Realm")
    } finally {
        if (VERBOSE) Log.d(QUERY_TAG, CLOSE_REALM_MSG)
        realm.close()
    }
    return results
}

/**
 * Perform a query with Realm and return a single result.
 *
 * @return the RealmModel instance resulting from the query or null if nothing was found
 */
inline fun <T: RealmModel> queryOneWithRealm(query: (realm: Realm) -> T) : T? {
    val realm = Realm.getDefaultInstance()
    val realmResult = query(realm)
    var result: T? = null
    try {
        result = realm.copyFromRealm(realmResult)
    } catch (e: IllegalArgumentException) {
        Log.d(QUERY_TAG, "No object found in Realm")
    } finally {
        if (VERBOSE) Log.d(QUERY_TAG, CLOSE_REALM_MSG)
        realm.close()
    }
    return result
}