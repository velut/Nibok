package com.nibokapp.nibok.extension

import com.nibokapp.nibok.data.db.Conversation
import com.nibokapp.nibok.data.db.ExternalUser
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import io.realm.Realm

/**
 * Extensions for repositories.
 */

/*
 * USER
 */

/**
 * Get local user.
 *
 * @return the User if it exits, null otherwise
 */
fun Realm.getLocalUser() : User? =
        this.where(User::class.java).findFirst()

/**
 * Get an external user by id.
 *
 * @param userId the external user's id
 *
 * @return the ExternalUser if it exits, null otherwise
 */
fun Realm.getExternalUserById(userId: Long) : ExternalUser? =
        this.where(ExternalUser::class.java).equalTo("id", userId).findFirst()

/*
 * BOOK INSERTION
 */

/**
 * Get book insertion by id.
 *
 * @param insertionId the insertion's id
 *
 * @return the Insertion if it exits, null otherwise
 */
fun Realm.getBookInsertionById(insertionId: Long) : Insertion? =
        this.where(Insertion::class.java).equalTo("id", insertionId).findFirst()

/*
 * CONVERSATION
 */

/**
 * Get conversation by id.
 *
 * @param conversationId the conversation's id
 *
 * @return the Conversation if it exits, null otherwise
 */
fun Realm.getConversationById(conversationId: Long) : Conversation? =
        this.where(Conversation::class.java).equalTo("id", conversationId).findFirst()

/**
 * Get the conversation between the two given users.
 *
 * @param localUserId the id of the local user
 * @param partnerId the id of the conversation's partner
 *
 * @return the Conversation between the two users if it exits, null otherwise
 */
fun Realm.getConversationBetweenUsers(localUserId: Long, partnerId: Long) : Conversation? =
        this.where(Conversation::class.java)
                .equalTo("userId", localUserId)
                .equalTo("partner.id", partnerId)
                .findFirst()