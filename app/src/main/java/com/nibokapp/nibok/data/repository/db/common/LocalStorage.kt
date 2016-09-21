package com.nibokapp.nibok.data.repository.db.common

import io.realm.RealmModel

/**
 * Interface for local repositories storing data
 */
interface LocalStorage<in T: RealmModel> {

    /**
     * Store the given list of items.
     *
     * @param items the items to store
     */
    fun storeItems(items : List<T>)
}