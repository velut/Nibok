package com.nibokapp.nibok.domain.mapper.user

import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.domain.model.UserModel

/**
 * User mapper implementation.
 */
class UserMapper() : UserMapperInterface {

    override fun convertUserToDomain(user: ExternalUser): UserModel = with(user) {
        UserModel(
                username = username,
                avatar = avatar
        )
    }

    override fun convertUserFromDomain(user: UserModel): ExternalUser = with(user) {
        ExternalUser(
                username = username,
                avatar = avatar
        )
    }
}