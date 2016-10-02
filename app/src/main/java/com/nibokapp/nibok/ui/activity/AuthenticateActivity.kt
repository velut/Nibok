package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.AuthInputValidator
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.hideKeyboardListener
import com.nibokapp.nibok.ui.filter.getAlphanumericFilter
import com.nibokapp.nibok.ui.presenter.AuthPresenter
import kotlinx.android.synthetic.main.activity_authenticate.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * AuthenticateActivity.
 * It provides a way for a guest to authenticate to the platform.
 * Authentication can be achieved either through logging in or by signing up to the platform.
 */
class AuthenticateActivity(
        val authPresenter: AuthPresenter = AuthPresenter(),
        val authInputValidator: AuthInputValidator = AuthInputValidator()
) : AppCompatActivity() {

    companion object {
        private val TAG = AuthenticateActivity::class.java.simpleName

        /**
         * Keys for bundle saving and restoring
         */
        private val KEY_AUTH_VIEW = "$TAG:showLogin"
    }

    // Default view is the login one. True -> Login; False -> Sign Up
    private var showLogin: Boolean = true

    private val logViewName: String
        get() = if (showLogin) "login view" else "sign up view"

    private var alertDialog: MaterialDialog? = null

    /*
     * INPUTS
     */

    // Username input
    private val username: String
        get() = inputUsername.text.toString()

    // Password input
    private val password: String
        get() = inputPasswordPrimary.text.toString()

    // Confirmation password, present only in sign up
    private val confirmationPassword: String
        get() = inputPasswordSecondary.text.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_AUTH_VIEW, showLogin)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        alertDialog?.dismiss()
    }

    private fun addAuthViewSwitchListener() {
        alternativeLink.setOnClickListener {
            showLogin = !showLogin
            Log.d(TAG, "Switching to $logViewName")
            showCurrentView()
        }
    }

    private fun addAuthButtonListener() {
        btnAuthenticate.setOnClickListener {
            authenticate()
        }
    }

    /**
     * Perform user authentication.
     *
     * Given a valid username and password use the to either login or sign up,
     * depending on the current shown view.
     *
     * If authentication was successful terminate the activity.
     * If authentication failed show a dialog stating so.
     */
    private fun authenticate() {

        if (hasInputErrors() || hasInvalidInput()) return

        doAsync {
            val authenticated =
                    if (showLogin) {
                        authPresenter.login(username, password)
                    } else {
                        authPresenter.signUp(username, password)
                    }
            uiThread {
                if (authenticated) {
                    finish()
                } else {
                    alertDialog = getAuthFailedDialog()
                    alertDialog?.show()
                }
            }
        }
    }

    /**
     * Update the view accordingly to show either the login or the sign up option.
     */
    private fun showCurrentView() {
        Log.d(TAG, "Showing $logViewName")

        validateUsername()
        validatePrimaryPassword()

        if (showLogin) {
            hideConfirmationPasswordInput()
        } else {
            showConfirmationPasswordInput()
        }

        updateButtons()
    }

    private fun hideConfirmationPasswordInput() {
        inputPasswordSecondaryLayout.apply {
            isEnabled = false
            visibility = View.GONE
            error = null
        }
        inputPasswordSecondary.text.clear()
    }

    private fun showConfirmationPasswordInput() {
        inputPasswordSecondaryLayout.apply {
            isEnabled = true
            visibility = View.VISIBLE
        }
        validateSecondaryPassword()
    }

    private fun updateButtons() {
        if (showLogin) {
            btnAuthenticate.text = getString(R.string.login)
            alternativeLink.text = getString(R.string.link_sign_up)
        } else {
            btnAuthenticate.text = getString(R.string.sign_up)
            alternativeLink.text = getString(R.string.link_login)
        }
    }

    private fun addHideKeyboardListener() {
        authFormContainer.setOnTouchListener { view, motionEvent ->
            hideKeyboardListener(motionEvent, view, this)
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

    /**
     * Validate the current username and set errors if needed.
     */
    private fun validateUsername() {

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
            checkUsernameAvailability()
        }
    }

    /**
     * Check if the current username is available
     * and update the username input view accordingly.
     */
    private fun checkUsernameAvailability() {
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

    /**
     * Setup input filters and validator for primary password.
     */
    private fun setupPrimaryPasswordInput() {
        inputPasswordPrimary.afterTextChanged {
            validatePrimaryPassword()
        }
    }
    /**
     * Validate the current password and set errors if needed.
     */
    private fun validatePrimaryPassword() {

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

    /**
     * Validate the current confirmation password and set errors if needed.
     */
    private fun validateSecondaryPassword() {

        if (confirmationPassword.isEmpty()) return

        if (confirmationPassword == password) {
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
     * contains input errors.
     * This is signaled by errors set on the input layout containers.
     *
     * @return true if the current view has any input errors, false if no errors were found
     */
    private fun hasInputErrors(): Boolean {
        val usernameError = inputUsernameLayout.error != null
        val primaryPasswordError = inputPasswordPrimaryLayout.error != null
        val hasError = usernameError || primaryPasswordError

        return if (showLogin) {
            hasError
        } else {
            val hasSecondaryPasswordError = inputPasswordSecondaryLayout.error != null
            hasError || hasSecondaryPasswordError
        }
    }

    /**
     * Check if any of the inputs relevant to the current view (login or sign up)
     * contains invalid input.
     *
     * @return true if the current view has any invalid input, false otherwise
     */
    private fun hasInvalidInput(): Boolean {
        val isInvalid = username.isEmpty() || password.isEmpty()
        return if (showLogin) {
            isInvalid
        } else {
            isInvalid || password != confirmationPassword
        }
    }

    /**
     * Get a dialog signaling the login or sign up fail.
     *
     * @return a MaterialDialog
     */
    private fun getAuthFailedDialog(): MaterialDialog {

        val dialogTitle = if (showLogin) {
            getString(R.string.title_login_fail)
        } else {
            getString(R.string.title_sign_up_fail)
        }

        val dialogContent = if (showLogin) {
            getString(R.string.content_login_fail)
        } else {
            getString(R.string.content_sign_up_fail)
        }

        return MaterialDialog.Builder(this)
                .title(dialogTitle)
                .content(dialogContent)
                .positiveText(getString(android.R.string.ok))
                .build()
    }
}
