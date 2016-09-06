package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.ChatAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import java.util.*

class ChatFragment() : Fragment() {

    companion object {
        private val TAG = ChatFragment::class.java.simpleName
        val CONVERSATION_ID = "$TAG:conversationId"
    }

    val chatAdapter = ChatAdapter(getUserId())
    val chatLayoutManager = LinearLayoutManager(context)


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_chat)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO remove test messages
        chatAdapter.addMessages((1..10).map {
            val date = Calendar.getInstance()
            date.add(Calendar.DATE, -10 + it)
            val content = arrayOf("Lorem Ipsum", "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem IpsumLorem IpsumLorem IpsumLorem Ipsum")
            val user = it % 2
            ChatMessageModel(1L, user.toLong(), content[user], date.time)
        })

        setupChatMessagesView()
        addSendButtonLister()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun addSendButtonLister() {
        btnSendMessage.setOnClickListener {
            val messageText = chatInputText.text.trim().toString()
            chatInputText.text.clear()
            if (messageText.isNotEmpty()) {
                // TODO retrieve real ids and send message
                val message = ChatMessageModel(0, getUserId(), messageText, Calendar.getInstance().time)
                val messagePosition = chatAdapter.addMessage(message)
                chatMessagesView.smoothScrollToPosition(messagePosition)
            }
        }
    }

    /**
     * Get the id of the local user.
     *
     * @return the id of the local user
     */
    private fun getUserId(): Long = UserManager.getUserId()

    private fun setupChatMessagesView() {
        Log.d(TAG, "Setting up chat messages view")
        chatMessagesView.apply {
            setHasFixedSize(true)
            // Show last element (auto scroll to bottom)
            chatLayoutManager.stackFromEnd = true
            layoutManager = chatLayoutManager
            adapter = chatAdapter
        }
    }
}

