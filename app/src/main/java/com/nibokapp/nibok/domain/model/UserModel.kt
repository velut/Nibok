package com.nibokapp.nibok.domain.model

/**
 * Schema representing a user.
 *
 * @param id the id of the user
 * @param name the name of the user
 * @param avatar the source for the user's avatar
 */
data class UserModel(
        val id: Long,
        val name: String,
        val avatar: String
)