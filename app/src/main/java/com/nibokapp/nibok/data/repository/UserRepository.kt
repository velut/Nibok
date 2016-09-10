package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.extension.executeRealmTransaction
import com.nibokapp.nibok.extension.queryOneWithRealm

/**
 * Repository singleton for the local user.
 */
object UserRepository {

    const private val TAG = "UserRepository"

    /**
     * Create the local user instance in the local db if necessary.
     *
     * @return true if the user was created, false otherwise
     */
    fun createLocalUser() {
        while (!localUserExists()) {
            Log.d(TAG, "Local user does not exist, trying to create one")
            executeRealmTransaction {
                // Insert user in the db
                val newUser = it.createObject(User::class.java)
                newUser.id = 1 // TODO id from server/auth
            }
        }
        Log.d(TAG, "Local user created")
    }

    /**
     * Get the id of the local user if the user exists.
     *
     * @return the user's id if the user exists, an IllegalStateException if the user does not exist
     */
    fun getLocalUserId(): Long = getLocalUser()?.id ?:
            throw IllegalStateException(
                    "Local user does not exist. Call createLocalUser() before retrieving data")

    /**
     * Get the local user from the database.
     *
     * @return the user if it exists, null otherwise
     */
     fun getLocalUser() : User? =
            queryOneWithRealm { it.where(User::class.java).findFirst() }

    /**
     * Check if the local user exists in the DB
     *
     * @return true if the user exists, false otherwise
     */
    fun localUserExists() : Boolean = getLocalUser() != null
}