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



    /*
     * MESSAGES
     */

    /*
     * EXTENSIONS
     */

    private fun BaasDocument.sendAndGrantAccess(accessLevel: Grant, accessedBy: String): Boolean {
        var sent = false
        this.saveSync().onSuccess {
            Log.d(TAG, "Document saved on server, granting access")
            sent = it.grantAllSync(accessLevel, accessedBy).isSuccess
        }
        return sent
    }

    private fun BaasDocument.sendForPublicReading(): Boolean {
        return this.sendAndGrantAccess(Grant.READ, Role.ANONYMOUS)
    }
}
