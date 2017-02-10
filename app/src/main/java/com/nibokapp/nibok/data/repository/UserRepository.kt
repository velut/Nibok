package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.extension.executeRealmTransaction
import com.nibokapp.nibok.extension.getLocalUser
import com.nibokapp.nibok.extension.queryOneRealm
import com.nibokapp.nibok.extension.toRealmStringList

/**
 * Repository singleton for the local user.
 */
object UserRepository : UserRepositoryInterface {

    const private val TAG = "UserRepository"

    override fun createLocalUser(userId: String, savedInsertionsIds: List<String>): Boolean {
        if (localUserExists()) return true
        Log.d(TAG, "Local user does not exist, trying to create one")
        executeRealmTransaction {
            // Insert user in the db
            val newUser = it.createObject(User::class.java, userId)
            newUser.savedInsertionsIds.addAll(savedInsertionsIds.toRealmStringList())
            Log.d(TAG, "Created new user: ${newUser.username}")
        }
        return localUserExists()
    }

    override fun removeLocalUser(): Boolean {
        if (!localUserExists()) {
            Log.d(TAG, "Local user does not exist. Cannot remove it.")
            return false
        }

        Log.d(TAG, "Removing local user")
        executeRealmTransaction {
            val user = it.getLocalUser()
            user?.deleteFromRealm()
        }

        return localUserExists()
    }

    override fun getLocalUserId(): String {
        val userId = getLocalUser()?.username
        return userId ?: throw IllegalStateException("Local user does not exist. Call createLocalUser() before retrieving data")
    }

    override fun getLocalUser(): User? {
        return queryOneRealm { it.where(User::class.java).findFirst() }
    }

    override fun localUserExists(): Boolean = getLocalUser() != null
}