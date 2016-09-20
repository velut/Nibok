package com.nibokapp.nibok.authentication.common

/**
 * Interface for user authenticators.
 */
interface AuthenticatorInterface {

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
     * Logout the currently logged in user if it exists.
     *
     * @return true if the user was correctly logged out, false otherwise
     */
    fun logout() : Boolean

    /**
     * Check if there is a currently logged in user or not.
     *
     * @return true if there is a currently logged in user, false otherwise
     */
    fun currentUserExists() : Boolean

}

