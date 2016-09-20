package com.nibokapp.nibok.ui.presenter

import com.nibokapp.nibok.authentication.Authenticator
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.domain.command.user.RequestLocalUserIdCommand

/**
 * Presenter for authentication related operations.
 */
class AuthPresenter(
        val authenticator: AuthenticatorInterface = Authenticator
) {

    /**
     * Login as a returning user with the given username and password.
     *
     * @param username the user's unique username
     * @param password the user's password
     *
     * @return true if the login was successful, false otherwise
     */
    fun login(username: String, password: String) : Boolean =
            authenticator.login(username, password)

    /**
     * Logout the currently logged in user if it exists.
     *
     * @return true if the user was correctly logged out, false otherwise
     */
    fun logout() : Boolean =
            authenticator.logout()

    /**
     * Sign up as a new user with the given username and password.
     *
     * @param username the user's unique username
     * @param password the user's password
     *
     * @return true if the sign up was successful, false otherwise
     */
    fun signUp(username: String, password: String) : Boolean =
            authenticator.signUp(username, password)

    /**
     * Check if there is a currently logged in user or not.
     *
     * @return true if there is a currently logged in user, false otherwise
     */
    fun loggedUserExists() : Boolean =
            authenticator.currentUserExists()

    /**
     * Get the id of the currently logged in user.
     *
     * @return the user id of the currently logged in user if it exists, null otherwise
     */
    fun getLoggedUserId(): String? =
            if (loggedUserExists())
                RequestLocalUserIdCommand().execute()
            else null

    /**
     * Check if the given username is available or not.
     *
     * @return true if the username is available, false otherwise
     */
    fun isUsernameAvailable(username: String) : Boolean =
            authenticator.isUsernameAvailable(username)
}
