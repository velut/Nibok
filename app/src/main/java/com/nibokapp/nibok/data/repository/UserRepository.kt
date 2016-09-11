package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.extension.executeRealmTransaction
import com.nibokapp.nibok.extension.queryOneWithRealm

/**
 * Repository singleton for the local user.
 */
object UserRepository : UserRepositoryInterface {

    const private val TAG = "UserRepository"

    override fun createLocalUser() {
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

    override fun getLocalUserId(): Long = getLocalUser()?.id ?:
            throw IllegalStateException(
                    "Local user does not exist. Call createLocalUser() before retrieving data")

    override fun getLocalUser() : User? =
            queryOneWithRealm { it.where(User::class.java).findFirst() }

    override fun localUserExists() : Boolean = getLocalUser() != null
}