package com.nibokapp.nibok.data.repository.common

import com.nibokapp.nibok.data.db.User


interface UserRepositoryInterface {

    /**
     * Create the local user instance in the local db if necessary.
     *
     * @param userId the id of the user
     * @param savedInsertionsIds the list of ids of the insertions saved by the user
     *
     * @return true if the local user exists, false otherwise
     */
    fun createLocalUser(userId: String, savedInsertionsIds: List<String> = emptyList()): Boolean

    /**
     * Remove the local user instance in the local db if necessary.
     *
     * @return true if the local user instance was removed successfully, false otherwise
     */
    fun removeLocalUser(): Boolean

    /**
     * Get the id of the local user if the user exists.
     *
     * @return the user's id if the user exists, an IllegalStateException if the user does not exist
     */
    fun getLocalUserId(): String

    /**
     * Get the local user from the database.
     *
     * @return the user if it exists, null otherwise
     */
    fun getLocalUser(): User?

    /**
     * Check if the local user exists in the DB
     *
     * @return true if the user exists, false otherwise
     */
    fun localUserExists(): Boolean
}