package com.nibokapp.nibok.authentication

import android.util.Log
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.baasbox.android.Rest
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.extension.getSavedInsertionsIdList
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

    private val loggedUser: BaasUser?
        get() = BaasUser.current()


    override fun signUp(username: String, password: String): Boolean {
        Log.d(TAG, "Sign up user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)
        user.init()

        val isRegistered = user.signupSync().onSuccess {
            localUserRepository.createLocalUser(it.name)
        }
        Log.d(TAG, "User: $user signed up: $isRegistered")
        return isRegistered
    }

    override fun login(username: String, password: String): Boolean {
        Log.d(TAG, "Login user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)

        val isLoggedIn = user.loginSync().onSuccess {
            localUserRepository.createLocalUser(it.name, it.getSavedInsertionsIdList())
        }
        Log.d(TAG, "User: $username logged in: $isLoggedIn")
        return isLoggedIn
    }

    override fun logout(): Boolean {
        val user = loggedUser
        if (user == null) {
            Log.d(TAG, "No user to logout")
            return false
        }
        val username = user.name
        val loggedOut = user.logoutSync().isSuccess
        if (loggedOut) {
            Log.d(TAG, "User: $username correctly logged out")
            localUserRepository.removeLocalUser()
        } else {
            Log.d(TAG, "Could not log out user: $username")
        }
        return loggedOut
    }

    override fun loggedUserExists(): Boolean {
        val user = loggedUser ?: return false
        if (!localUserExists()) {
            Log.d(TAG, "Logged used exists but local user does not, creating one")
            localUserRepository.createLocalUser(user.name, user.getSavedInsertionsIdList())
        }
        return true
    }

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
