package com.nibokapp.nibok.extension

import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.baasbox.android.json.JsonObject
import com.nibokapp.nibok.data.repository.server.common.ServerConstants

/**
 * Extensions handling BaasUser related operations.
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

/**
 * Get the JsonArray containing the ids of the insertions saved by this user.
 * 
 * @return a JsonArray that might be empty if the user did not save any insertion
 */
fun BaasUser.getSavedInsertionsArray() : JsonArray {
    val scope = this.getPrivateScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.SAVED_INSERTIONS, JsonArray())
}

/**
 * Get the JsonArray containing the ids of the insertions published by this user.
 *
 * @return a JsonArray that might be empty if the user did not publish any insertion
 */
fun BaasUser.getPublishedInsertionsArray() : JsonArray {
    val scope = this.getPublicScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.PUBLISHED_INSERTIONS, JsonArray())
}

/**
 * Get the JsonArray containing the ids of the conversations in which this user is participating in.
 *
 * @return a JsonArray that might be empty if the user did not save any insertion
 */
fun BaasUser.getConversationsArray() : JsonArray {
    val scope = this.getPrivateScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.CONVERSATIONS, JsonArray())
}

/**
 * Get the String representing the source of this user's avatar.
 *
 * @return a String that might be the empty string if the user has no avatar.
 */
fun BaasUser.getAvatar() : String {
    val scope = this.getPublicScope() ?: return ""
    return scope.getString(ServerConstants.AVATAR, "")
}