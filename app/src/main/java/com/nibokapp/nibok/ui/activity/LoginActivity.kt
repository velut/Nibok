package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Login activity.
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName

        /**
         * Keys for bundle saving and restoring
         */
        private val KEY_AUTH_VIEW = "$TAG:showLogin"
    }

    // Default view is the login one
    private var showLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showLogin = savedInstanceState?.getBoolean(KEY_AUTH_VIEW, true) ?: true

        showCurrentView()

        addHideKeyboardListener()
        addAuthViewSwitchListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Saving Login: $showLogin")
        outState.putBoolean(KEY_AUTH_VIEW, showLogin)
        super.onSaveInstanceState(outState)
    }

    private fun showCurrentView() {
        Log.d(TAG, "Showing " + if (showLogin) "login" else "sign up")

        if (showLogin) {
            // Hide and clear secondary password
            inputPasswordSecondaryLayout.apply {
                isEnabled = false
                visibility = View.GONE
            }
            inputPasswordSecondary.text.clear()

            // Change button and link
            btnAuthenticate.text = getString(R.string.login)
            alternativeLink.text = getString(R.string.link_sign_up)
        } else {
            // Show secondary password
            inputPasswordSecondaryLayout.apply {
                isEnabled = true
                visibility = View.VISIBLE
            }

            // Change button and link
            btnAuthenticate.text = getString(R.string.sign_up)
            alternativeLink.text = getString(R.string.link_login)
        }
    }

    private fun addAuthViewSwitchListener() {
        alternativeLink.setOnClickListener {
            showLogin = !showLogin
            Log.d(TAG, "Switching to " + if (showLogin) "login" else "sign up")
            showCurrentView()
        }
    }

    private fun addHideKeyboardListener() {
        authFormContainer.setOnTouchListener { view, motionEvent ->
            val action = MotionEventCompat.getActionMasked(motionEvent)
            if (action == MotionEvent.ACTION_DOWN) { // If the view was tapped
                view.hideSoftKeyboard(this)
            }
            false
        }
    }
}
