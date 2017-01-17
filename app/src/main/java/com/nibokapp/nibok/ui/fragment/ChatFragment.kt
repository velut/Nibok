package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ChatMessageModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.chat.ChatAdapter
import com.nibokapp.nibok.ui.presenter.ChatPresenter
import kotlinx.android.synthetic.main.fragment_chat.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 * ChatFragment hosts the chat view and is responsible for its operations.
 */
class ChatFragment(
        val presenter: ChatPresenter = ChatPresenter()
) : Fragment() {

    companion object {
        private val TAG = ChatFragment::class.java.simpleName

        /**
         * Key for arguments passing.
         */
        val CONVERSATION_ID = "$TAG:conversationId"

        /**
         * Timer constants.
         * Delay before start is 1 second, execution period is 2 seconds
         */
        const private val TIMER_DELAY =  (1 * 1000).toLong()
        const private val TIMER_PERIOD = (2 * 1000).toLong()
    }

    private var actionBar: ActionBar? = null

    private var conversationId: String? = null

    private val userId by lazy { presenter.getUserId() }

    private val chatAdapter = ChatAdapter(userId)
    private val chatLayoutManager = LinearLayoutManager(context)

    private lateinit var checkNewMessagesTimer: Timer

    private lateinit var partnerName: String
    private val partnerNamePlaceholder by lazy { getString(R.string.placeholder_chat_partner) }


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
        hostingActivity.apply {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        actionBar = hostingActivity.supportActionBar
        actionBar?.title = getString(R.string.placeholder_chat)

        // Retrieve conversationId
        conversationId = arguments?.getString(ChatFragment.CONVERSATION_ID)
        setupConversation()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "Timer: Starting to check for new messages")
        checkNewMessagesTimer = fixedRateTimer(initialDelay = TIMER_DELAY, period = TIMER_PERIOD) {
                Log.d(TAG, "Timer: Checking for new messages")
                val lastMessage = chatAdapter.getLastMessage()
                val newMessages = if (lastMessage != null) {
                    presenter.getNewerMessages(lastMessage)
                } else {
                    conversationId?.let { presenter.getConversationMessages(it) } ?: emptyList()
                }
                activity?.runOnUiThread { // Activity may be null
                    addNewMessages(newMessages)
                }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "Timer: Stopping, no more check for new messages ")
        checkNewMessagesTimer.cancel()
    }

    private fun setupConversation() {
        conversationId?.let {
            Log.d(TAG, "Got conversationId: $it")
            doAsync {
                partnerName = presenter.getConversationPartnerName(it) ?: partnerNamePlaceholder
                val messages = presenter.getConversationMessages(it)
                uiThread {
                    actionBar?.title = partnerName
                    chatAdapter.addMessages(messages)
                }
            }
        }
    }

    private fun addNewMessages(newMessages: List<ChatMessageModel>) {

        if (newMessages.isEmpty()) return

        val lm = chatMessagesView.layoutManager as LinearLayoutManager
        val viewIsAtBottom = lm.findLastVisibleItemPosition() == (lm.itemCount - 1)
        val newBottomPosition = chatAdapter.addMessages(newMessages) ?: return

        // After adding the new messages if the view was at the bottom scroll to the newer bottom
        // to make the new messages visible, instead if the user is reading older messages simply
        // alert him that the partner has responded
        if (viewIsAtBottom) {
            chatMessagesView.smoothScrollToPosition(newBottomPosition)
        } else {
            val toastMessage =
                    String.format(resources.getString(R.string.partner_reply), partnerName)
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

        if (messageText.isEmpty()) {
            // Clear input if it contained only whitespace and return
            chatInputText.text.clear()
            return
        }

        val currentConversationId = conversationId ?: return

        val message = buildMessage(currentConversationId, messageText)

        doAsync {
            val messageId = presenter.sendMessage(message)
            uiThread {
                if (messageId != null) {
                    chatInputText.text.clear()
                    val sentMessage = message.copy(messageId)
                    val messagePosition = chatAdapter.addMessage(sentMessage)
                    messagePosition?.let {
                        chatMessagesView.smoothScrollToPosition(it)
                    }
                } else {
                    context.toast(R.string.error_message_not_sent)
                }
            }
        }

    }

    private fun buildMessage(currentConversationId: String, messageText: String): ChatMessageModel {
        return ChatMessageModel(
                "", // id set after being sent
                currentConversationId,
                userId,
                messageText,
                Date()
        )
    }

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

