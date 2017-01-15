package com.nibokapp.nibok.server.send

import android.util.Log
import com.baasbox.android.BaasACL
import com.baasbox.android.BaasDocument
import com.baasbox.android.Grant
import com.baasbox.android.Role
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

    override fun sendBookDocument(document: BaasDocument): Pair<Boolean, String?> {
        Log.d(TAG, "Sending book to server")
        val sent = document.sendForPublicReading()
        return Pair(sent, document.getIdOrNull(sent))
    }

    /*
     * INSERTIONS
     */

    override fun sendInsertionDocument(document: BaasDocument): Pair<Boolean, String?> {
        Log.d(TAG, "Sending insertion to server")
        val sent = document.sendForPublicReading()
        return Pair(sent, document.getIdOrNull(sent))
    }

    /*
     * CONVERSATIONS
     */

    override fun sendConversationDocument(document: BaasDocument, partnerId: String): Pair<Boolean, String?> {
        Log.d(TAG, "Sending conversation to server")
        // ACL allowing the conversation's partner to read and update the conversation document
        val acl = BaasACL.grantUser(partnerId, Grant.READ, Grant.UPDATE)
        val sent = document.saveSync(acl).isSuccess
        return Pair(sent, document.getIdOrNull(sent))
    }

    /*
     * MESSAGES
     */

    override fun sendMessageDocument(document: BaasDocument, recipientId: String): Pair<Boolean, String?> {
        Log.d(TAG, "Sending message to server")
        // ACL allowing the message's recipient to read the message document
        val acl = BaasACL.grantUser(recipientId, Grant.READ)
        val sent = document.saveSync(acl).isSuccess
        return Pair(sent, document.getIdOrNull(sent))
    }

    /*
     * EXTENSIONS
     */

    /**
     * Send this BaasDocument and let it be readable by everyone (including anonymous users).
     */
    private fun BaasDocument.sendForPublicReading(): Boolean {
        // Access Control List allowing anonymous users to read this document
        val acl = BaasACL.grantRole(Role.ANONYMOUS, Grant.READ)
        return this.saveSync(acl).isSuccess
    }

    /**
     * Get the id of this document if it was saved, null otherwise.
     */
    private fun BaasDocument.getIdOrNull(sent: Boolean): String? = if (sent) this.id else null
}
