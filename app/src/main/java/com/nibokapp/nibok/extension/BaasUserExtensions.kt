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
            }
            getPublicScope()?.apply {
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
fun BaasUser.getSavedInsertionsArray(): JsonArray {
    val scope = this.getPrivateScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.SAVED_INSERTIONS, JsonArray())
}

/**
 * Get the list of ids of the insertions saved by this user.
 *
 * @return a list of strings representing the insertions' ids
 */
fun BaasUser.getSavedInsertionsIdList(): List<String> {
    return this.getSavedInsertionsArray().filterIsInstance<String>()
}

/**
 * Get the String representing the source of this user's avatar.
 *
 * @return a String that might be the empty string if the user has no avatar.
 */
fun BaasUser.getAvatar(): String {
    val scope = this.getPublicScope() ?: return ""
    return scope.getString(ServerConstants.AVATAR, "")
}

/**
 * Toggle the save status of the insertion with the given id.
 *
 * @param insertionId the id of the insertion to save or remove
 *
 * @return true if the insertion is currently saved, false otherwise
 */
fun BaasUser.toggleInsertionSaveStatus(insertionId: String): Boolean {
    val savedInsertionIds = getSavedInsertionsArray()
    val copy = savedInsertionIds.copy()

    val insertionIndex = savedInsertionIds.indexOf(insertionId)
    val previouslySaved = insertionIndex != -1

    if (previouslySaved) {
        savedInsertionIds.remove(insertionIndex)
    } else {
        savedInsertionIds.add(insertionId)
    }

    // Synchronize with the server
    val saved = saveSync().onSuccessReturn { insertionId in it.getSavedInsertionsArray() }

    if (saved == null) { // Request failed, restore previous save status
        savedInsertionIds.apply {
            clear()
            append(copy)
        }
    }

    return saved ?: previouslySaved // If the request fails return the previous save status
}
