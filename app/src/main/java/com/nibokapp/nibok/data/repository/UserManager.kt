package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.withRealm

/**
 * Manager class for the local user data.
 */
class UserManager {

    companion object {
        private val TAG = UserManager::class.java.simpleName
    }

    /**
     * Create the local user instance in the local db if necessary.
     */
    fun createUser() {
        if (!isUserCreated()) {
            Log.d(TAG, "Local user does not exist, creating one")
            withRealm {
                it.executeTransaction {
                    val user = it.createObject(User::class.java)
                    user.id = 1
                }
            }
        }
    }

    /**
     * Check if the local user exists in the local db.
     *
     * @return true if the user exists, false otherwise
     */
    fun isUserCreated() : Boolean {
        var userExists = false
        val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
        user?.let {
            userExists = true
        }
        return userExists
    }
}