package com.nibokapp.nibok.data.repository.common

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User


interface UserRepositoryInterface {

    /**
     * Sign up as a new user with the given username and password.
     *
     * @param username the user's unique username
     * @param password the user's password
     *
     * @return true if the sign up was successful, false otherwise
     */
    fun signUp(username: String, password: String) : Boolean

    /**
     * Login as a returning user with the given username and password.
     *
     * @param username the user's unique username
     * @param password the user's password
     *
     * @return true if the login was successful, false otherwise
     */
    fun login(username: String, password: String) : Boolean

    /**
     * Create the local user instance in the local db if necessary.
     */
    fun createLocalUser(userId: String, savedInsertions: List<Insertion> = emptyList(),
                        publishedInsertions: List<Insertion> = emptyList(),
                        conversations: List<Conversation> = emptyList())

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
    fun getLocalUser() : User?

    /**
     * Check if the local user exists in the DB
     *
     * @return true if the user exists, false otherwise
     */
    fun localUserExists() : Boolean
}