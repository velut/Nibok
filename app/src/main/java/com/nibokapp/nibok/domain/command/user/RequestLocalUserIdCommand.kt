package com.nibokapp.nibok.domain.command.user

import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.domain.command.common.Command

/**
 * Request the id of the local user.
 *
 */
class RequestLocalUserIdCommand(
        val userRepository: UserRepositoryInterface = UserRepository
) : Command<Long> {

    override fun execute(): Long =
            userRepository.getLocalUserId()
}