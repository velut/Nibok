package com.nibokapp.nibok.domain.model

/**
 * Schema representing a user.
 *
 * @param username the name of the user
 * @param avatar the source for the user's avatar
 */
data class UserModel(
        val username: String,
        val avatar: String
)