package com.nibokapp.nibok.data.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for a conversation between the local user and an external user (partner).
 * Used by the local database.
 *
 * @param id the id of the conversation
 * @param partnerId the id of the external user
 * @param partnerAvatar the source for the external user's avatar
 * @param messages the list of messages exchanged in the conversation - One to many
 */
open class Conversation(

        @PrimaryKey open var id: Long = 0,

        open var partnerId: Long = 0,

        open var partnerAvatar: String = "",

        open var messages: RealmList<Message> = RealmList()

) : RealmObject() {}
