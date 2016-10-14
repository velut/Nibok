package com.nibokapp.nibok.extension

import com.baasbox.android.BaasResult
import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.baasbox.android.json.JsonObject
import com.nibokapp.nibok.data.repository.server.common.ServerConstants

/**
 * Extensions handling Baas related operations.
 */

/**
 * Given a BaasResult on a type T if the result is successful execute the given
 * function on T.
 *
 * @param func a function that operates on the data of type T contained in the result
 *
 * @return true if the result was successful and 'func' was called,
 * false if the result was not successful and 'func' was not called
 */
inline fun <reified T> BaasResult<T>.onSuccess(func: (T) -> Unit) : Boolean {
    if (this.isSuccess && this.value() != null) {
        func(this.value())
        return true
    }
    return false
}

/*
 * BAAS USER
 */

/**
 * Initialize a new BaasUser by putting empty collections in the fields
 * relevant to the platform.
 */
fun BaasUser.init() {
    this.apply {
        with(ServerConstants) {

            getPrivateScope()?.apply {
                put(SAVED_INSERTIONS, JsonArray())
                put(CONVERSATIONS, JsonArray())
            }

            getPublicScope()?.apply {
                put(PUBLISHED_INSERTIONS, JsonArray())
                put(AVATAR, "")
            }

        }
    }
}

/**
 * Get the private scope of this BaasUser.
 *
 * @return the private scope if the current user has access to it, null otherwise
 */
fun BaasUser.getPrivateScope(): JsonObject? = this.getScope(BaasUser.Scope.PRIVATE)

/**
 * Get the public scope of this BaasUser.
 *
 * @return the private scope if the current user has access to it, null otherwise
 */
fun BaasUser.getPublicScope(): JsonObject? = this.getScope(BaasUser.Scope.PUBLIC)