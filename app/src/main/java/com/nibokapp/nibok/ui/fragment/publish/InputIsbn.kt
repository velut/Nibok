package com.nibokapp.nibok.ui.fragment.publish

import android.os.Bundle
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.rule.IsbnValidator
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.hideSoftKeyboard
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_isbn.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

/**
 * Publishing fragment managing ISBN codes input.
 *
 * If a valid ISBN code was submitted as input it sends a request to find book data about the book
 * with the given ISBN code and then lets the user proceed to the next screen.
 *
 * @param isbnValidator the [IsbnValidator] used to validate ISBN codes
 */
class InputIsbn(
        private val isbnValidator: IsbnValidator = IsbnValidator()
) : BasePublishFragment() {

    companion object {
        private val TAG = InputIsbn::class.java.simpleName
    }

    /**
     * Current ISBN code coming from input form.
     */
    private val currentIsbn: String
        get() = inputISBN.getStringText()

    private var bookData: BookData? = null

    private var progressDialog: MaterialDialog? = null
    private var errorDialog: MaterialDialog? = null

    /**
     * Observable that tracks if a request for book data
     * associated to the Isbn code given as input is in progress.
     * Starting status is false (request not active).
     *
     * If a request was started show the progress dialog.
     * If a request has ended dismiss the progress dialog.
     */
    private var bookDataRequestInProgress: Boolean by Delegates.observable(false) {
        prop, wasActive, isActive ->
        if (!wasActive && isActive) {
            Log.d(TAG, "Book data request is in progress")
            showProgressDialog()
        } else if (wasActive && !isActive) {
            Log.d(TAG, "Book data request was completed")
            progressDialog?.dismiss()
        }
    }


    override fun getFragmentLayout() : Int = R.layout.fragment_publish_input_isbn

    override fun getInputContainer() : View = inputISBNContainer

    override fun getDialogs(): List<MaterialDialog?> = listOf(progressDialog, errorDialog)

    override fun hasValidData() : Boolean = true

    override fun saveData() = Unit

    override fun triggerInputsUpdate() {
        // The user decided to skip isbn input -> clear eventual errors
        inputISBNLayout.error = null
    }

    override fun setupInput() {
        setupIsbnInput()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain Instance in order to correctly perform the book data request and display dialogs
        retainInstance = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (bookDataRequestInProgress) showProgressDialog()
    }

    private fun setupIsbnInput() {
        inputISBN.afterTextChanged {
            validateInputIsbn()
        }
    }

    /**
     * Validate the current ISBN code with the validator.
     */
    private fun validateInputIsbn() {
        if (isbnValidator.isIsbnLengthValid(currentIsbn)) {
            handleValidLengthIsbn()
        } else {
            handleInvalidLengthIsbn()
        }
    }

    /**
     * Handle a valid length ISBN code.
     *
     * If the ISBN code is a valid one and no code is currently set
     * try to retrieve data about the book.
     */
    private fun handleValidLengthIsbn() {
        if (isbnValidator.isIsbnValid(currentIsbn)) {
            if (publishProcessManager.isIsbnSet()) return
            Log.d(TAG, "Valid ISBN set: $currentIsbn")
            inputISBNLayout.removeInputError()
            view?.hideSoftKeyboard(context)
            retrieveBookData()
        } else {
            Log.d(TAG, "Invalid ISBN set: $currentIsbn")
            inputISBNLayout.setInputError(R.string.error_invalid_isbn)
        }
    }

    private fun handleInvalidLengthIsbn() {
        inputISBNLayout.setInputError(R.string.error_input_isbn)
        publishProcessManager.resetBookData()
    }

    /**
     * Set the current ISBN code as the one to be used for the insertion and
     * try to retrieve data about the book associated to such ISBN code.
     */
    private fun retrieveBookData() {
        publishProcessManager.setIsbn(currentIsbn)
        bookDataRequestInProgress = true
        doAsync {
            bookData = presenter.getBookDataByIsbn(currentIsbn)
            uiThread {
                bookDataRequestInProgress = false
                setBookData(bookData)
            }
        }
    }

    /**
     * If data about a book was found set it as the data to be used in the insertion
     * and proceed to the next screen, otherwise show an error dialog.
     */
    private fun setBookData(bookData: BookData?) {
        if (bookData != null) {
            publishProcessManager.setBookData(bookData)
            Log.d(TAG, "Book data found and set")
            nextScreen()
        } else {
            Log.d(TAG, "No book data was found, showing error dialog")
            showErrorDialog()
        }
    }

    /**
     * If necessary build an indeterminate, non cancelable, progress dialog and then show it.
     */
    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = MaterialDialog.Builder(context)
                    .content(R.string.progress_get_book_info)
                    .progress(true, 0) // Indeterminate progress dialog
                    .cancelable(false) // Clicking outside the dialog does not close it
                    .build()
        }
        progressDialog?.show()
    }

    /**
     * If necessary build an error dialog signaling that book data associated to a certain Isbn code
     * was not found and then show it.
     * Once the dialog is accepted proceed to the next screen.
     */
    private fun showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = MaterialDialog.Builder(context)
                    .title(R.string.dialog_title_book_info_not_found)
                    .content(R.string.dialog_content_book_info_not_found)
                    .positiveText(R.string.str_continue)
                    .onPositive { materialDialog, dialogAction ->  nextScreen() }
                    .build()
        }
        errorDialog?.show()
    }
}