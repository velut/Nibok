package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.AuthInputValidator
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.hideSoftKeyboard
import com.nibokapp.nibok.ui.filter.getAlphanumericFilter
import com.nibokapp.nibok.ui.presenter.AuthPresenter
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Login activity.
 * Used by a guest to either login into the application or sign up.
 */
class LoginActivity(
        val authPresenter: AuthPresenter = AuthPresenter(),
        val authInputValidator: AuthInputValidator = AuthInputValidator()
) : AppCompatActivity() {

    companion object {
        private val TAG = LoginActivity::class.java.simpleName

        /**
         * Keys for bundle saving and restoring
         */
        private val KEY_AUTH_VIEW = "$TAG:showLogin"
    }

    // Default view is the login one
    private var showLogin = true

    private var alertDialog: MaterialDialog? = null


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
        addAuthButtonListener()

        setupUsernameInput()
        setupPrimaryPasswordInput()
        setupSecondaryPasswordInput()

    }

    private fun addAuthButtonListener() {
        btnAuthenticate.setOnClickListener {
            if (showLogin)
                login()
            else
                signUp()
        }
    }

    private fun login() {

        if (hasInputErrors()) return

        val username = inputUsername.text.toString()
        val password = inputPasswordPrimary.text.toString()

        if (username.isEmpty() || password. isEmpty()) return

        val context = this

        doAsync {
            val loggedIn = authPresenter.login(username, password)
            if (loggedIn) {
                uiThread { finish() }
            } else {
                uiThread {
                    alertDialog = MaterialDialog.Builder(context)
                            .title(getString(R.string.title_login_fail))
                            .content(getString(R.string.content_login_fail))
                            .positiveText(getString(android.R.string.ok))
                            .build()
                    alertDialog?.show()
                }
            }
        }
    }

    private fun signUp() {

        if (hasInputErrors()) return

        val username = inputUsername.text.toString()
        val password = inputPasswordPrimary.text.toString()
        val confirmPassword = inputPasswordSecondary.text.toString()

        if (username.isEmpty() || password. isEmpty() || password != confirmPassword) return

        val context = this

        doAsync {
            val signedUp = authPresenter.signUp(username, password)
            if (signedUp) {
                uiThread { finish() }
            } else {
                uiThread {
                    alertDialog = MaterialDialog.Builder(context)
                            .title(getString(R.string.title_sign_up_fail))
                            .content(getString(R.string.content_sign_up_fail))
                            .positiveText(getString(android.R.string.ok))
                            .build()
                    alertDialog?.show()
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "Saving Login: $showLogin")
        outState.putBoolean(KEY_AUTH_VIEW, showLogin)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    private fun showCurrentView() {
        Log.d(TAG, "Showing " + if (showLogin) "login" else "sign up")

        validateUsername()
        validatePrimaryPassword()

        if (showLogin) {
            // Hide and clear secondary password
            inputPasswordSecondaryLayout.apply {
                isEnabled = false
                visibility = View.GONE
                error = null
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
            validateSecondaryPassword()

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

        inputUsername.afterTextChanged {
            validateUsername()
        }

        inputUsername.filters = arrayOf(getAlphanumericFilter(),
                android.text.InputFilter.LengthFilter(AuthInputValidator.MAX_USERNAME_LENGTH))
    }

    private fun validateUsername() {

        val username = inputUsername.text.toString()

        if (username.isEmpty()) return

        // On new input reset error
        inputUsernameLayout.error = null

        // Min length not valid error
        if (!authInputValidator.isUsernameMinLengthValid(username)) {
            val usernameTooShortError =
                    String.format(getString(R.string.error_username_too_short),
                            AuthInputValidator.MIN_USERNAME_LENGTH)
            inputUsernameLayout.apply {
                error = usernameTooShortError
                requestFocus()
            }
            return
        }

        // Username already taken error. Only in Sign Up
        if (!showLogin) {
            doAsync {
                val available = authPresenter.isUsernameAvailable(username)
                uiThread {
                    if (!available) {
                        inputUsernameLayout.apply {
                            error = getString(R.string.username_already_taken)
                            requestFocus()
                        }
                    }
                }
            }
        }
    }

    /**
     * Setup input filters and validator for primary password.
     */
    private fun setupPrimaryPasswordInput() {
        inputPasswordPrimary.afterTextChanged {
            validatePrimaryPassword()
        }
    }

    private fun validatePrimaryPassword() {

        val password = inputPasswordPrimary.text.toString()

        if (password.isEmpty()) return

        if (authInputValidator.isPasswordMinLengthValid(password)) {
            inputPasswordPrimaryLayout.error = null
        } else {
            val passwordTooShortError = String.format(getString(R.string.error_password_too_short),
                    AuthInputValidator.MIN_PASSWORD_LENGTH)
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
        inputPasswordSecondary.afterTextChanged {
            validateSecondaryPassword()
        }
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

    /**
     * Check if any of the inputs relevant to the current view (login or sign up)
     * contains invalid input.
     * This is signaled by errors set on the input layout containers.
     *
     * @return true if the current view has any input errors, false if no errors were found
     */
    private fun hasInputErrors(): Boolean {
        val usernameError = inputUsernameLayout.error != null
        val primaryPasswordError = inputPasswordPrimary.error != null
        val hasError = usernameError || primaryPasswordError

        return if (showLogin) {
            hasError
        } else {
            val hasSecondaryPasswordError = inputPasswordSecondary.error != null
            hasError || hasSecondaryPasswordError
        }
    }
}
