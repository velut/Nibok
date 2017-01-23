package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.os.Handler
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
import com.nibokapp.nibok.ui.behavior.InfiniteScrollListener
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
         * Delay before timer starts is 1 second, timer's execution period is 2 seconds.
         */
        private const val TIMER_DELAY =  (1 * 1000).toLong()
        private const val TIMER_PERIOD = (2 * 1000).toLong()

        /**
         * Delay before a message can be sent again.
         */
        private const val SEND_DELAY = 200L
    }

    private var actionBar: ActionBar? = null

    private var conversationId: String? = null

    private lateinit var userId: String

    private lateinit var chatAdapter: ChatAdapter
    private val chatLayoutManager = LinearLayoutManager(context)

    private var setupCompleted: Boolean = false

    private var checkNewMessagesTimer: Timer? = null

    private lateinit var partnerName: String
    private val partnerNamePlaceholder by lazy { getString(R.string.placeholder_chat_partner) }

    private var lastSentTime: Long? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_chat)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = try {
            presenter.getUserId()
        } catch (e: IllegalStateException) {
            Log.d(TAG, "No user id set for the local user, cannot proceed")

            // If no userId was found return, setupCompleted will be false
            // and further setup operations won't be executed
            return
        }

        chatAdapter = ChatAdapter(userId)
        setupChatMessagesView()
        addSendButtonLister()
        setupCompleted = true
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

        if (!setupCompleted) return

        // Retrieve conversationId
        conversationId = arguments?.getString(ChatFragment.CONVERSATION_ID)
        setupConversation()
    }

    override fun onStart() {
        super.onStart()

        if (!setupCompleted) return

        Log.d(TAG, "Timer: Starting to check for new messages")
        checkNewMessagesTimer = fixedRateTimer(initialDelay = TIMER_DELAY, period = TIMER_PERIOD) {
            Log.d(TAG, "Timer: checking for new messages")
            checkForNewMessages()
        }
    }

    override fun onStop() {
        super.onStop()
        checkNewMessagesTimer?.let {
            Log.d(TAG, "Timer: stopping; no more checking for new messages")
            it.cancel()
        }
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

    private fun checkForNewMessages() {
        val lastMessage = chatAdapter.getLastMessage()
        doAsync {
            val newMessages = if (lastMessage != null) {
                presenter.getNewerMessages(lastMessage)
            } else {
                conversationId?.let { presenter.getConversationMessages(it) } ?: emptyList()
            }

            // Activity may be null, e.g. when refresh is going on but activity was closed
            // When activity is null skip don't run addNewMessages() as it causes a NPE
            activity?.runOnUiThread {
                addNewMessages(newMessages)
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

        // Prevent rapid firing of the same message
        // A message can be sent only after SEND_DELAY time has passed
        // since the last time send message was called
        val canSend = (Date().time - (lastSentTime ?: 0L)) > SEND_DELAY
        if (!canSend) {
            Log.d(TAG, "Preventing possible duplicate message")
            return
        }
        lastSentTime = Date().time

        // Check if new messages arrived before sending our message
        // This prevents lost updates when two users send messages close in time
        checkForNewMessages()

        // Build and send the message
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
            clearOnScrollListeners()
            addOnScrollListener(InfiniteScrollListener(chatLayoutManager, false) {
                loadOlderMessages()
            })
        }
    }

    private fun loadOlderMessages() {
        Log.d(TAG, "Loading older messages for conversation: $conversationId")

        val oldestMessage = chatAdapter.getFirstMessage() ?: return

        Handler().post {
            doAsync {
                val olderMessages = presenter.getOlderMessages(oldestMessage)
                uiThread {
                    if (olderMessages.isNotEmpty()) {
                        Log.d(TAG, "Adding older messages for conversation: $conversationId")
                        chatAdapter.addOlderMessages(olderMessages)
                    } else {
                        Log.d(TAG, "No older messages found")
                    }
                }
            }
        }
    }
}

