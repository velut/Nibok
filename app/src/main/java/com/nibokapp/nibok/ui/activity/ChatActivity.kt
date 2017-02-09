package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.chat.ChatFragment

/**
 * ChatActivity.
 * This activity simply hosts the ChatFragment.
 */
class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // If restoring do not create overlapping fragments
        if (savedInstanceState != null) {
            return
        }

        val chatFragment = ChatFragment()
        // Pass eventual intent extras to the fragment
        chatFragment.arguments = intent.extras

        // Add the fragment to the container
        supportFragmentManager.beginTransaction()
                .add(R.id.chatFragmentContainer, chatFragment)
                .commit()
    }
}
