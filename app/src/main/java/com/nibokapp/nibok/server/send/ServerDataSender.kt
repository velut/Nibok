package com.nibokapp.nibok.server.send

import android.util.Log
import com.baasbox.android.BaasDocument
import com.baasbox.android.Grant
import com.baasbox.android.Role
import com.nibokapp.nibok.extension.onSuccess
import com.nibokapp.nibok.server.send.common.ServerDataSenderInterface

class ServerDataSender : ServerDataSenderInterface {

    companion object {
        private val TAG = ServerDataSender::class.java.simpleName
    }

    /*
     * USER
     */


    /*
     * BOOK DATA
     */

    override fun sendBookDocument(document: BaasDocument): Boolean {
        Log.d(TAG, "Sending book to server")
        return document.sendForPublicReading()
    }

    /*
     * INSERTIONS
     */

    override fun sendInsertionDocument(document: BaasDocument): Boolean {
        Log.d(TAG, "Sending insertion to server")
        return document.sendForPublicReading()
    }

    /*
     * CONVERSATIONS
     */

    override fun sendConversationDocument(document: BaasDocument, partnerId: String): Boolean {
        Log.d(TAG, "Sending conversation to server")
        return document.sendAndGrantOneAccess(Grant.UPDATE, partnerId)
    }

    /*
     * MESSAGES
     */

    /*
     * EXTENSIONS
     */

    /**
     * Send this BaasDocument to the server and once saved successfully execute the given function.
     */
    private fun BaasDocument.sendAndDo(func: (BaasDocument) -> Boolean): Boolean {
        var sent = false
        this.saveSync().onSuccess { sent = func(it) }
        return sent
    }

    /**
     * Send this BaasDocument to the server and grant the given access level to the given user.
     *
     * @param accessLevel the access level granted to the user for the document
     * @param accessingUserId the id of the user accessing the document
     *
     * @return true if the BaasDocument was correctly sent and setup
     */
    private fun BaasDocument.sendAndGrantOneAccess(accessLevel: Grant, accessingUserId: String): Boolean {
        return this.sendAndDo {
            Log.d(TAG, "Document saved on server, granting access")
            it.grantSync(accessLevel, accessingUserId).isSuccess
        }
    }

    /**
     * Send this BaasDocument to the server and grant the given access level to the specified user roles.
     *
     * @param accessLevel the access level granted to the user for the document
     * @param accessedBy the category of users accessing the document
     *
     * @return true if the BaasDocument was correctly sent and setup
     */
    private fun BaasDocument.sendAndGrantAllAccess(accessLevel: Grant, accessedBy: String): Boolean {
        return this.sendAndDo {
            Log.d(TAG, "Document saved on server, granting access")
            it.grantAllSync(accessLevel, accessedBy).isSuccess
        }
    }

    /**
     * Send this BaasDocument and let it be readable by everyone (including anonymous users).
     */
    private fun BaasDocument.sendForPublicReading(): Boolean {
        return this.sendAndGrantAllAccess(Grant.READ, Role.ANONYMOUS)
    }
}
