package com.nibokapp.nibok.extension

import com.baasbox.android.BaasUser
import com.baasbox.android.json.JsonArray
import com.baasbox.android.json.JsonObject
import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.repository.server.common.ServerConstants
import com.nibokapp.nibok.server.provider.ServerDataProvider
import com.nibokapp.nibok.server.provider.common.ServerDataProviderInterface

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
 * Get the JsonArray containing the ids of the insertions published by this user.
 *
 * @return a JsonArray that might be empty if the user did not publish any insertion
 */
fun BaasUser.getPublishedInsertionsArray(): JsonArray {
    val scope = this.getPublicScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.PUBLISHED_INSERTIONS, JsonArray())
}

/**
 * Get the JsonArray containing the ids of the conversations in which this user is participating in.
 *
 * @return a JsonArray that might be empty if the user did not save any insertion
 */
fun BaasUser.getConversationsArray(): JsonArray {
    val scope = this.getPrivateScope() ?: return JsonArray()
    return scope.getArray(ServerConstants.CONVERSATIONS, JsonArray())
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
 * Get the list of insertions saved by the user.
 *
 * @return a list of Insertion
 */
fun BaasUser.getSavedInsertions(): List<Insertion> =
        getInsertionsFromArray(getSavedInsertionsArray())

/**
 * Get the list of insertions published by the user.
 *
 * @return a list of Insertion
 */
fun BaasUser.getPublishedInsertions(): List<Insertion> =
        getInsertionsFromArray(getPublishedInsertionsArray())

/**
 * Get the list of conversations in which the user is participating.
 *
 * @return a list of Conversation
 */
fun BaasUser.getConversations(): List<Conversation> =
        getConversationsFromArray(getConversationsArray())

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

/**
 * Add the given insertion id to the list of the insertions published by the user.
 *
 * @param insertionId the id of the insertion that the user has published
 *
 * @return true if the insertion id was successfully added, false otherwise
 */
fun BaasUser.addPublishedInsertion(insertionId: String): Boolean {
    val publishedInsertionsIds = getPublishedInsertionsArray()

    val insertionIndex = publishedInsertionsIds.indexOf(insertionId)
    val previouslyPublished = insertionIndex != -1

    if (previouslyPublished) return true

    publishedInsertionsIds.add(insertionId)

    val published = saveSync().onSuccessReturn { insertionId in it.getPublishedInsertionsArray() }

    if (published == null) { // Request failed, restore previous status
        val insPos = publishedInsertionsIds.indexOf(insertionId)
        publishedInsertionsIds.remove(insPos)
    }

    return published ?: false
}

/**
 * Get the list of insertions corresponding to the ids stored in the given JsonArray.
 *
 * @param array the array that contains the ids of the insertions
 * @param provider the provider used to fetch data from the server.
 * ServerDataProvider is used by default.
 *
 * @return a list of Insertion
 */
private fun getInsertionsFromArray(array: JsonArray,
                                   provider: ServerDataProviderInterface = ServerDataProvider()): List<Insertion> {
    return provider.getInsertionListFromIds(array)
}

/**
 * Get the list of conversations corresponding to the ids stored in the given JsonArray.
 *
 * @param array the array that contains the ids of the conversations
 * @param provider the provider used to fetch data from the server.
 * ServerDataProvider is used by default.
 *
 * @return a list of Conversation
 */
private fun getConversationsFromArray(array: JsonArray,
                                      provider: ServerDataProviderInterface = ServerDataProvider()): List<Conversation> {
    return provider.getConversationListFromIds(array)
}