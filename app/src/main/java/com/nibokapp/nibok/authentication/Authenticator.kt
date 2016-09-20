package com.nibokapp.nibok.authentication

import android.util.Log
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.nibokapp.nibok.authentication.common.AuthenticatorInterface
import com.nibokapp.nibok.data.repository.UserRepository
import com.nibokapp.nibok.data.repository.common.UserRepositoryInterface
import com.nibokapp.nibok.data.repository.server.ServerConstants
import com.nibokapp.nibok.extension.getConversations
import com.nibokapp.nibok.extension.getPublishedInsertions
import com.nibokapp.nibok.extension.getSavedInsertions

object Authenticator : AuthenticatorInterface {

    const private val TAG = "Authenticator"

    private val localUserRepository: UserRepositoryInterface = UserRepository


    override fun signUp(username: String, password: String): Boolean {
        Log.d(TAG, "Sign up user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)
        user.apply {

            getScope(BaasUser.Scope.PRIVATE)
                    .put(ServerConstants.SAVED_INSERTIONS, JsonArray())
                    .put(ServerConstants.CONVERSATIONS, JsonArray())

            getScope(BaasUser.Scope.PUBLIC)
                    .put(ServerConstants.PUBLISHED_INSERTIONS, JsonArray())
                    .put(ServerConstants.AVATAR, "")
        }

        val result = user.signupSync()

        if (result.isSuccess) {
            val userData = result.value()
            userData?.let {
                localUserRepository.createLocalUser(it.name)
            }
            return true
        } else {
            return false
        }
    }

    override fun login(username: String, password: String): Boolean {
        Log.d(TAG, "Login user: $username")

        val user = BaasUser.withUserName(username).setPassword(password)

        val result = user.loginSync()

        if (result.isSuccess) {
            val userData = result.value()
            userData?.let {
                with(it) {
                    localUserRepository
                            .createLocalUser(name,
                                    getSavedInsertions(),
                                    getPublishedInsertions(),
                                    getConversations())
                }
            }
            return true
        } else {
            return false
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
        val result = currentUser.logoutSync()

        if (result.isSuccess) {
            Log.d(TAG, "Logged out user: $username")
            localUserRepository.removeLocalUser()
            return true
        } else {
            Log.d(TAG, "Could not log out user: $username")
            return false
        }
    }

    override fun currentUserExists(): Boolean = BaasUser.current() != null
}
