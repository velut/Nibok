package com.nibokapp.nibok.data.repository.common

import com.nibokapp.nibok.data.db.User


interface UserRepositoryInterface {

    /**
     * Create the local user instance in the local db if necessary.
     */
    fun createLocalUser()

    /**
     * Get the id of the local user if the user exists.
     *
     * @return the user's id if the user exists, an IllegalStateException if the user does not exist
     */
    fun getLocalUserId(): Long

    /**
     * Get the local user from the database.
     *
     * @return the user if it exists, null otherwise
     */
    fun getLocalUser() : User?

    /**
     * Check if the local user exists in the DB
     *
     * @return true if the user exists, false otherwise
     */
    fun localUserExists() : Boolean
}