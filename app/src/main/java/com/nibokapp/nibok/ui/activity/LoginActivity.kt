package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.AuthenticationValidator
import com.nibokapp.nibok.extension.hideSoftKeyboard
import com.nibokapp.nibok.ui.filter.getAlphanumericFilter
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Login activity.
 * Used by a guest to either login into the application or sign up.
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

    // Authentication validator
    private val authValidator = AuthenticationValidator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Remove focus from edit text forms
        authFormContainer.requestFocus()

        // Restore previous view if it exists, otherwise show login
        showLogin = savedInstanceState?.getBoolean(KEY_AUTH_VIEW, true) ?: true

        showCurrentView()

        addHideKeyboardListener()
        addAuthViewSwitchListener()

        setupUsernameInput()
        setupPrimaryPasswordInput()
        setupSecondaryPasswordInput()
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

    /**
     * Setup input filters and validator for username.
     */
    private fun setupUsernameInput() {

        inputUsername.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validateUsername()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
        )

        inputUsername.filters = arrayOf(getAlphanumericFilter(),
                android.text.InputFilter.LengthFilter(AuthenticationValidator.MAX_USERNAME_LENGTH))
    }

    private fun validateUsername() {

        val username = inputUsername.text.toString()

        if (username.isEmpty()) return

        if (authValidator.isUsernameMinLengthValid(username)) {
            inputUsernameLayout.error = null
        } else {
            val usernameTooShortError = String.format(getString(R.string.error_username_too_short),
                    AuthenticationValidator.MIN_USERNAME_LENGTH)
            inputUsernameLayout.apply {
                error = usernameTooShortError
                requestFocus()
            }
        }

        if (!showLogin) {
            // TODO Alert of username already in use on sign up
        }
    }

    /**
     * Setup input filters and validator for primary password.
     */
    private fun setupPrimaryPasswordInput() {
        inputPasswordPrimary.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validatePrimaryPassword()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
        )
    }

    private fun validatePrimaryPassword() {

        val password = inputPasswordPrimary.text.toString()

        if (password.isEmpty()) return

        if (authValidator.isPasswordMinLengthValid(password)) {
            inputPasswordPrimaryLayout.error = null
        } else {
            val passwordTooShortError = String.format(getString(R.string.error_password_too_short),
                    AuthenticationValidator.MIN_PASSWORD_LENGTH)
            inputPasswordPrimaryLayout.apply {
                error = passwordTooShortError
                requestFocus()
            }
        }
    }

    /**
     * Setup input filters and validator for secondary (confirmation) password.
     */
    private fun setupSecondaryPasswordInput() {
        inputPasswordSecondary.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validateSecondaryPassword()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
        )
    }

    private fun validateSecondaryPassword() {

        val password = inputPasswordPrimary.text.toString()
        val confirmPassword = inputPasswordSecondary.text.toString()

        if (confirmPassword.isEmpty()) return

        if (confirmPassword == password) {
            inputPasswordSecondaryLayout.error = null
        } else {
            val differentPasswords = getString(R.string.error_password_do_not_match)
            inputPasswordSecondaryLayout.apply {
                error = differentPasswords
                requestFocus()
            }
        }
    }
}
