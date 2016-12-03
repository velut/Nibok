package com.nibokapp.nibok.authentication

import android.util.Log
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.baasbox.android.Rest
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.*
import org.jetbrains.anko.doAsync

/**
 * Authenticator.
 * This object is responsible for authentication related operations.
 */
object Authenticator : AuthenticatorInterface {

    const private val TAG = "Authenticator"

    private val localUserRepository: UserRepositoryInterface = UserRepository

    private val loggedUser: BaasUser?
        get() = BaasUser.current()


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

        // Remove the local user, even if logout from the server will be unsuccessful
        Log.d(TAG, "Removing eventual local user")
        localUserRepository.removeLocalUser()

        // Try to log out user from the server.
        // Logout may fail if the server is unreachable.
        // Async in order to let the caller go on not waiting for the logout
        Log.d(TAG, "Trying to logout user from the server")
        loggedUser?.let {
            doAsync {
                val loggedOut = it.logoutSync().isSuccess
                Log.d(TAG, if (loggedOut) "Logged out"
                            else "Could not log out" + " user: ${it.name}")
            }
        }

        // Return true as the local user does not exist anymore
        // and a failed server logout does not impact a successive login
        return true
    }

    override fun currentUserExists(): Boolean = localUserExists()

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

    private fun localUserExists(): Boolean = localUserRepository.localUserExists()
}
