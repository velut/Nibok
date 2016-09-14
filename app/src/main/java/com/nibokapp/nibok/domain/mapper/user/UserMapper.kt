package com.nibokapp.nibok.domain.mapper.user

import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.domain.model.UserModel

class UserMapper() : UserMapperInterface {

    override fun convertUserToDomain(user: ExternalUser): UserModel = with(user) {
        UserModel(
                id = id,
                name = name,
                avatar = avatar
        )
    }

    override fun convertUserFromDomain(user: UserModel): ExternalUser = with(user) {
        ExternalUser(
                id = id,
                name = name,
                avatar = avatar
        )
    }
}