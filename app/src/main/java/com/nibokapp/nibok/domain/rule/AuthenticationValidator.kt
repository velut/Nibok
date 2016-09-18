package com.nibokapp.nibok.domain.rule

/**
 * Validator for username and password inputs.
 */
class AuthenticationValidator {

    companion object {
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 15

        const val MIN_PASSWORD_LENGTH = 8
    }

    /**
     * Check if the username minimum length is correct.
     *
     * @param username the string representing the username
     *
     * @return true if the minimum length is correct, false otherwise
     */
    fun isUsernameMinLengthValid(username: String) =
            username.length >= MIN_USERNAME_LENGTH

    /**
     * Check if the password minimum length is correct.
     *
     * @param password the string representing the password
     *
     * @return true if the minimum length is correct, false otherwise
     */
    fun isPasswordMinLengthValid(password: String) =
            password.length >= MIN_PASSWORD_LENGTH
}
