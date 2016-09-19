package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.extension.executeRealmTransaction
import com.nibokapp.nibok.extension.queryOneWithRealm

/**
 * Repository singleton for the local user.
 */
object UserRepository : UserRepositoryInterface {

    const private val TAG = "UserRepository"

    override fun signUp(username: String, password: String) : Boolean {
        throw UnsupportedOperationException("Sign up can only be done with the server")
    }

    override fun login(username: String, password: String): Boolean {
        throw UnsupportedOperationException("Login can only be done with the server")
    }

    override fun createLocalUser(userId: String,
                                 savedInsertions: List<Insertion>,
                                 publishedInsertions: List<Insertion>,
                                 conversations: List<Conversation>) {
        while (!localUserExists()) {
            Log.d(TAG, "Local user does not exist, trying to create one")
            executeRealmTransaction {
                // Insert user in the db
                val newUser = it.createObject(User::class.java)
                newUser.apply {
                    username = userId
                    this.savedInsertions.addAll(savedInsertions)
                    this.publishedInsertions.addAll(publishedInsertions)
                    this.conversations.addAll(conversations)
                }
            }
        }
        Log.d(TAG, "Local user created")
    }

    override fun getLocalUserId(): String = getLocalUser()?.username ?:
            throw IllegalStateException(
                    "Local user does not exist. Call createLocalUser() before retrieving data")

    override fun getLocalUser() : User? =
            queryOneWithRealm { it.where(User::class.java).findFirst() }

    override fun localUserExists() : Boolean = getLocalUser() != null
}