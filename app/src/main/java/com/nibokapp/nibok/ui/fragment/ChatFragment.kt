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
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.ChatAdapter
import com.nibokapp.nibok.ui.presenter.ChatPresenter
import kotlinx.android.synthetic.main.fragment_chat.*
import org.jetbrains.anko.toast
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ChatFragment(val presenter: ChatPresenter = ChatPresenter()) : Fragment() {

    companion object {
        private val TAG = ChatFragment::class.java.simpleName
        val CONVERSATION_ID = "$TAG:conversationId"

        /**
         * Timer constants.
         * Delay before start is 2 seconds, execution period is 10 seconds
         */
        const private val TIMER_DELAY =  (2 * 1000).toLong()
        const private val TIMER_PERIOD = (10 * 1000).toLong()
    }

    private val chatAdapter = ChatAdapter(getUserId())
    private val chatLayoutManager = LinearLayoutManager(context)

    lateinit var checkNewMessagesTimer : Timer


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_chat)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChatMessagesView()
        addSendButtonLister()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the conversation's id and add messages
        arguments?.let {
            val conversationId = it.getLong(ChatFragment.CONVERSATION_ID)
            if (!conversationId.equals(0L)) { // Exclude default case of getLong()
                Log.d(TAG, "Got conversationId: $conversationId")
                val messages = presenter.getConversationMessages(conversationId)
                chatAdapter.addMessages(messages)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Timer: Starting to check for new messages")
        checkNewMessagesTimer = fixedRateTimer(initialDelay = TIMER_DELAY, period = TIMER_PERIOD) {
            Log.d(TAG, "Timer: Checking for new messages")
            val lastMessage = chatAdapter.getLastMessage()
            lastMessage?.let {
                val newMessages = presenter.getNewerMessages(it)
                activity.runOnUiThread {
                    addNewMessages(newMessages)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Timer: Stopping, no more check for new messages ")
        checkNewMessagesTimer.cancel()
    }

    private fun addNewMessages(newMessages: List<ChatMessageModel>) {

        if (newMessages.isEmpty()) return

        val lm = chatMessagesView.layoutManager as LinearLayoutManager
        val viewIsAtBottom = lm.findLastVisibleItemPosition() == (lm.itemCount - 1)
        val newBottomPosition = chatAdapter.addMessages(newMessages)

        // After adding the new messages if the view was at the bottom scroll to the newer bottom
        // to make the new messages visible, instead if the user is reading older messages simply
        // alert him that the partner has responded
        if (viewIsAtBottom) {
            chatMessagesView.smoothScrollToPosition(newBottomPosition)
        } else {
            // TODO Use real partner name
            val toastMessage =
                    String.format(resources.getString(R.string.partner_reply), "PARTNER")
            context.toast(toastMessage)
        }
    }

    private fun addSendButtonLister() {
        btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText = chatInputText.text.trim().toString()
        if (messageText.isNotEmpty()) {
            // TODO retrieve real ids and send message
            val message = ChatMessageModel(0, getUserId(), messageText, Calendar.getInstance().time)
            val messageSent = presenter.sendMessage(message)
            if (messageSent) {
                chatInputText.text.clear()
                val messagePosition = chatAdapter.addMessage(message)
                chatMessagesView.smoothScrollToPosition(messagePosition)
            } else {
                context.toast(R.string.error_message_not_sent)
            }
        } else {
            // Clear input if it contained only whitespace
            chatInputText.text.clear()
        }
    }

    /**
     * Get the id of the local user.
     *
     * @return the id of the local user
     */
    private fun getUserId(): Long = presenter.getUserId()

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

