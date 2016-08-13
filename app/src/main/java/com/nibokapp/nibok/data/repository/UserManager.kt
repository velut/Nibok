package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.toNormalList
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

    fun hasUserSavedInsertions() : Boolean {
        var result = false
        if (!isUserCreated()) {
            return result
        }
        val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
        val savedInsertions = user?.savedInsertions
        savedInsertions?.let {
            result = true
        }
        return result
    }

    fun getUserSavedInsertions() : List<Insertion> {
        val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
        val savedBooks = user?.savedInsertions?.toNormalList()
        var results: List<Insertion> = emptyList()
        savedBooks?.let {
            results = it
        }
        Log.d(TAG, "Returning ${results.size} saved insertions")
        return results
    }

    fun toggleSaveInsertion(insertionId: Long) : Boolean {
        var saved = false

        if (hasUserSavedInsertions()) {
            withRealm {
                val insertion = it.where(Insertion::class.java).equalTo("id", insertionId).findFirst()
                val user = it.where(User::class.java).findFirst()
                val savedInsertions = user?.savedInsertions!!

                saved = savedInsertions.contains(insertion)
                it.executeTransaction {
                    if (!saved) {
                        savedInsertions.add(0, insertion)
                    } else {
                        savedInsertions.remove(insertion)
                    }
                }
                saved = savedInsertions.contains(insertion)
                Log.d(TAG, "Save status: $saved, saved size is ${savedInsertions.size}")
            }
        }
        return saved
    }
}