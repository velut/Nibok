package com.nibokapp.nibok.extension

import android.util.Log
import com.nibokapp.nibok.data.db.common.RealmString
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

val QUERY_TAG = "RealmDB_Query"

/**
 * Extension functions related to the local Realm database.
 */

/**
 * Convert a RealmList of RealmString into a normal String list.
 *
 * @return a list of strings
 */
fun RealmList<RealmString>.toStringList() : List<String> =
        this.map { it.value }

/**
 * Convert a String to a RealmString.
 */
fun String.toRealmString() : RealmString = RealmString(this)

/**
 * Convert a List of String into a RealmList of RealmString.
 *
 * @return a a RealmList of RealmString
 */
fun List<String>.toRealmStringList() : RealmList<RealmString> {
    val realmList = RealmList<RealmString>()
    this.forEach { realmList.add(it.toRealmString()) }
    return realmList
}

/**
 * Convert a List of RealmModel into a RealmList of RealmModel.
 *
 * @return a a RealmList of RealmModel
 */
fun <T : RealmModel> List<T>.toRealmList() : RealmList<T> {
    val realmList = RealmList<T>()
    this.forEach { realmList.add(it) }
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
    withRealm {
        it.executeTransaction { transaction(it) }
    }
}

/**
 * Perform a query with Realm and return the list of results.
 *
 * @param query the function performing the query
 *
 * @return the list of RealmModel instances representing the found results
 * or an empty list if no result was found
 */
inline fun <T: RealmModel> queryManyRealm(query: (realm: Realm) -> RealmResults<T>) : List<T> {
    var results: List<T> = emptyList()

    withRealm {
        val realmResults = query(it)
        results =
                try {
                    it.copyFromRealm(realmResults)
                } catch (e: IllegalArgumentException) {
                    Log.d(QUERY_TAG, "No list of objects found in Realm")
                    emptyList()
                }
    }

    return results
}

/**
 * Perform a query with Realm and return a single result.
 *
 * @param query the function performing the query
 *
 * @return the RealmModel instance resulting from the query or null if nothing was found
 */
inline fun <T: RealmModel> queryOneRealm(query: (realm: Realm) -> T) : T? {
    var result: T? = null

    withRealm {
        val realmResult = query(it)
        result =
                try {
                    it.copyFromRealm(realmResult)
                } catch (e: IllegalArgumentException) {
                    Log.d(QUERY_TAG, "No object found in Realm")
                    null
                }
    }

    return result
}