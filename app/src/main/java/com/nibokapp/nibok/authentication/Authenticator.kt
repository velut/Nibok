package com.nibokapp.nibok.authentication

import android.util.Log
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.baasbox.android.Rest
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.init
import com.nibokapp.nibok.extension.onSuccess
import com.nibokapp.nibok.extension.onSuccessReturn

/**
 * Authenticator.
 * This object is responsible for authentication related operations.
 */
object Authenticator : AuthenticatorInterface {

    const private val TAG = "Authenticator"

    private val localUserRepository: UserRepositoryInterface = UserRepository


    override fun signUp(username: String, password: String): Boolean {
        Log.d(TAG, "Sign up user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)
        user.init()

        return user.signupSync().onSuccess {
            localUserRepository.createLocalUser(it.name)
        }
    }

    override fun login(username: String, password: String): Boolean {
        Log.d(TAG, "Login user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)

        return user.loginSync().onSuccess {
            with(it) {
                localUserRepository
                        .createLocalUser(name,
                                getSavedInsertions(),
                                getPublishedInsertions(),
                                getConversations())
            }
        }
    }

    override fun logout(): Boolean {
        Log.d(TAG, "Logging out")
        val currentUser = BaasUser.current()

        if (currentUser == null) {
            Log.d(TAG, "No current user to logout")
            return false
        }

        val username = currentUser.name

        return currentUser.logoutSync().onSuccess {
            localUserRepository.removeLocalUser()
            Log.d(TAG, "Logged out user: $username")
        }
    }

    override fun currentUserExists(): Boolean = BaasUser.current() != null

    override fun isUsernameAvailable(username: String): Boolean {

        val result = BaasBox.rest().sync(
                Rest.Method.GET,
                "plugin/users.usernameAvailable?username=$username"
        )

        /*
         * Initially we assume that the username is available,
         * this is done so that in the case the request fails
         * we don't make the strong assumption that the username
         * is already taken.
         */
        val available = result.onSuccessReturn {
            it.getString(ServerConstants.DATA) == ServerConstants.USERNAME_AVAILABLE
        }
        return available ?: true
    }
}
