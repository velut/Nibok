package com.nibokapp.nibok.extension

import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import io.realm.Realm

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
fun Realm.getLocalUser() : User? =
        this.where(User::class.java).findFirst()

/*
 * BOOK INSERTION
 */

/**
 * Get book insertion by id.
 *
 * @param insertionId the insertion's id
 *
 * @return the Insertion if it exits, null otherwise
 */
fun Realm.getBookInsertionById(insertionId: Long) : Insertion? =
        this.where(Insertion::class.java).equalTo("id", insertionId).findFirst()