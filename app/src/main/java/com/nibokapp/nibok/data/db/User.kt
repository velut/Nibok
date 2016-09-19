package com.nibokapp.nibok.data.db

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Model class for the local user.
 * Used by the local database.
 *
 * @param username the username of the user. Primary key
 * @param savedInsertions the insertions saved by the user - One to many
 * @param publishedInsertions the insertions published by the user - One to many
 * @param conversations the conversations that the user has with other external users
 */
open class User(

        @PrimaryKey open var username: String = "",

        open var savedInsertions: RealmList<Insertion> = RealmList(),

        open var publishedInsertions: RealmList<Insertion> = RealmList(),

        open var conversations: RealmList<Conversation> = RealmList()

) : RealmObject() {}
