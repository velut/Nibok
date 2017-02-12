package com.nibokapp.nibok.extension

import com.nibokapp.nibok.data.db.User
import io.realm.*
import java.util.*

/**
 * Extensions for repositories.
 */

/*
 * USER
 */

/**
 * Get local user.
 *
 * @return the User if it exits, null otherwise
 */
fun Realm.getLocalUser(): User? {
    return this.where(User::class.java).findFirst()
}

/*
 * Common
 */

/**
 * Add to a query the condition that the RealmModel's date must be older than the given one.
 *
 * @param date the date used in the query
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.dateOlderThan(date: Date): RealmQuery<T> {
    return this.lessThanOrEqualTo("date", date)
}

/**
 * Add to a query the condition that the RealmModel's date must be newer than the given one.
 *
 * @param date the date used in the query
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.dateNewerThan(date: Date): RealmQuery<T> {
    return this.greaterThanOrEqualTo("date", date)
}

/**
 * Add to a query the condition that the RealmModel's id must be equal to the given id.
 *
 * @param id the id used in the query
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.idEqualTo(id: String): RealmQuery<T> {
    return this.equalTo("id", id)
}

/**
 * Add to a query the condition that the RealmModel's id must be different from the given id.
 *
 * @param id the id used in the query
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.idNotEqualTo(id: String): RealmQuery<T> {
    return this.notEqualTo("id", id)
}

/**
 * Add to a query the condition that the RealmModel's must be older
 * than the one with the given id and date.
 *
 * @param otherId the id of the other item
 * @param otherDate the date of the other item
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.olderThan(otherId: String, otherDate: Date): RealmQuery<T> {
    return this.idNotEqualTo(otherId).dateOlderThan(otherDate)
}

/**
 * Add to a query the condition that the RealmModel's must be newer
 * than the one with the given id and date.
 *
 * @param otherId the id of the other item
 * @param otherDate the date of the other item
 *
 * @return a RealmQuery
 */
fun <T : RealmModel> RealmQuery<T>.newerThan(otherId: String, otherDate: Date): RealmQuery<T> {
    return this.idNotEqualTo(otherId).dateNewerThan(otherDate)
}

/**
 * Find all the results and sort them by descending date (newest to oldest).
 */
fun <T : RealmModel> RealmQuery<T>.findAllSortedByDescendingDate(): RealmResults<T> {
    return this.findAllSorted("date", Sort.DESCENDING)
}

/**
 * Find all the results and sort them by ascending date (oldest to newest).
 */
fun <T : RealmModel> RealmQuery<T>.findAllSortedByAscendingDate(): RealmResults<T> {
    return this.findAllSorted("date", Sort.ASCENDING)
}