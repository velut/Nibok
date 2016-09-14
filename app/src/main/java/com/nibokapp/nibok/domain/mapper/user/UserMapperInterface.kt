package com.nibokapp.nibok.domain.mapper.user

import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.domain.model.UserModel

/**
 * Interface for Data Mappers operating on users.
 */
interface UserMapperInterface {

    /**
     * Convert ExternalUser to UserModel.
     *
     * @param user the external user object
     *
     * @return a UserModel
     */
    fun convertUserToDomain(user: ExternalUser): UserModel

    /**
     * Convert UserModel to ExternalUser.
     *
     * @param user the user model object
     *
     * @return an ExternalUser
     */
    fun convertUserFromDomain(user: UserModel): ExternalUser

}