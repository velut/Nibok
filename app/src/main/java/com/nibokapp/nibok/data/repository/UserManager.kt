package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.extension.queryOneWithRealm
import com.nibokapp.nibok.extension.toNormalList
import com.nibokapp.nibok.extension.withRealm

/**
 * Manager singleton for the local user data.
 */
object UserManager {

    private val TAG = "UserManager"

    /**
     * Signals if the local user exists and is ready for use.
     */
    private var userExists = false

    /**
     * Create the local user instance in the local db if necessary.
     */
    fun createUser() {
        if (!isUserCreated()) {
            Log.d(TAG, "Local user does not exist, creating one")
            withRealm {
                it.executeTransaction {
                    // Insert user in the db
                    val user = it.createObject(User::class.java)
                    user.id = 1
                }
            }
        }
        // Check that the user was inserted in the db and is ready for use
        val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
        user?.let {
            if (it.savedInsertions != null && it.publishedInsertions != null) {
                userExists = true
            }
        }
        if (userExists) Log.d(TAG, "Local User exists")
    }

    /**
     * Get the list of insertions saved by the user if such list is available.
     *
     * @return the list of insertions saved by the user
     * or an empty list if no saved insertions exists
     */
    fun getUserSavedInsertions() : List<Insertion> {
        if (userExists) {
            val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
            val savedInsertions = user!!.savedInsertions!!.toNormalList()
            Log.d(TAG, "Found ${savedInsertions.size} saved insertions")
            return savedInsertions
        } else {
            Log.d(TAG, "User does not exist, no saved insertions available")
            return emptyList()
        }
    }

    /**
     * Toggle the save status for an insertion either by adding it to the user saved insertions
     * if it was not already saved or by removing it if it was already saved.
     *
     * @param insertionId the id of the insertion to add or remove
     *
     * @return true if the insertion was saved false if it was removed
     */
    fun toggleSaveInsertion(insertionId: Long) : Boolean {
        var saved = false
        if (userExists) {
            withRealm {
                val insertion = it.where(Insertion::class.java).equalTo("id", insertionId).findFirst()
                val user = it.where(User::class.java).findFirst()
                val savedInsertions = user!!.savedInsertions!!

                saved = savedInsertions.contains(insertion)
                it.executeTransaction {
                    if (!saved) {
                        savedInsertions.add(0, insertion)
                    } else {
                        savedInsertions.remove(insertion)
                    }
                }
                saved = savedInsertions.contains(insertion)
                Log.d(TAG, "After toggle: Save status: $saved, saved size: ${savedInsertions.size}")
            }
        }
        return saved
    }

    /**
     * Check if the insertion with the given id was already saved by the user or not.
     *
     * @param insertionId the id of the insertion
     *
     * @return true if the insertion belongs to the saved insertions, false otherwise
     */
    fun isInsertionSaved(insertionId: Long) : Boolean {
        var saved = false
        if (userExists) {
            withRealm {
                val insertion = it.where(Insertion::class.java).equalTo("id", insertionId).findFirst()
                val user = it.where(User::class.java).findFirst()
                val savedInsertions = user!!.savedInsertions!!
                saved = savedInsertions.contains(insertion)
            }
        }
        return saved
    }

    /**
     * Check if the local user exists in the local db.
     *
     * @return true if the user exists, false otherwise
     */
    private fun isUserCreated() : Boolean {
        var created = false
        val user = queryOneWithRealm { it.where(User::class.java).findFirst() }
        user?.let {
            created = true
        }
        return created
    }
}