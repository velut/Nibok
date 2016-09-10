package com.nibokapp.nibok.data.repository

import android.util.Log
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.data.db.User
import com.nibokapp.nibok.extension.toNormalList
import com.nibokapp.nibok.extension.withRealm
import io.realm.Realm

/**
 * Repository for book insertions.
 */
object BookInsertionRepository {

    const private val TAG = "BookInsertionRepository"

    /**
     * Get the list of insertions saved by the user if such list is available.
     *
     * @return the list of insertions saved by the user
     * or an empty list if no saved insertions could be found
     */
    fun getSavedBookInsertionList() : List<Insertion> =
            UserRepository.getLocalUser()?.savedInsertions?.toNormalList() ?: emptyList()

    /**
     * Check if the book insertion with the given id is among the ones saved by the user or not.
     *
     * @param insertionId the id of the book insertion
     *
     * @return true if the book insertion belongs to the user's saved insertions, false otherwise
     */
    fun isBookInsertionSaved(insertionId: Long) : Boolean =
            insertionId in getSavedBookInsertionList().map { it.id }

    /**
     * Toggle the save status for an insertion either by adding it to the user saved insertions
     * if it was not already saved or by removing it if it was already saved.
     *
     * @param insertionId the id of the insertion to add or remove
     *
     * @return true if the insertion was saved false if it was removed
     */
    fun toggleBookInsertionSaveStatus(insertionId: Long) : Boolean {

        if (!UserRepository.localUserExists())
            throw IllegalStateException("Local user does not exist. Cannot save insertion")

        var saved = false

        withRealm {
            val insertion = it.getBookInsertionById(insertionId)
            val user = it.getLocalUser()
            val savedInsertions = user!!.savedInsertions

            saved = insertion in savedInsertions

            it.executeTransaction {
                if (!saved) {
                    savedInsertions.add(0, insertion)
                } else {
                    savedInsertions.remove(insertion)
                }
            }
            saved = insertion in savedInsertions
            Log.d(TAG, "After toggle: Save status: $saved, saved size: ${savedInsertions.size}")
        }
        return saved
    }

    private fun Realm.getBookInsertionById(insertionId: Long) : Insertion? =
            this.where(Insertion::class.java).equalTo("id", insertionId).findFirst()

    private fun Realm.getLocalUser() : User? =
            this.where(User::class.java).findFirst()
}