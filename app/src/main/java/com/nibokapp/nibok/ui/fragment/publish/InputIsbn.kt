package com.nibokapp.nibok.ui.fragment.publish

import android.util.Log
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.IsbnValidator
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_isbn.*

/**
 * Publishing fragment manging ISBN codes input.
 *
 * @param isbnValidator the [IsbnValidator] used to validate ISBN codes
 */
class InputIsbn(
        private val isbnValidator: IsbnValidator = IsbnValidator()
) : BasePublishFragment() {

    companion object {
        private val TAG = InputIsbn::class.java.simpleName
    }

    private val currentIsbn: String
        get() = inputISBN.getStringText()


    override fun getFragmentLayout() : Int = R.layout.fragment_publish_input_isbn

    override fun getInputContainer() : View = inputISBNContainer

    override fun hasValidData() : Boolean = true

    override fun triggerInputsUpdate() {
        // The user decided to skip isbn input -> clear eventual errors
        inputISBNLayout.error = null
    }

    override fun setupInput() {
        setupIsbnInput()
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
     */
    private fun handleValidLengthIsbn() {
        if (isbnValidator.isIsbnValid(currentIsbn)) {
            Log.d(TAG, "Valid ISBN set: $currentIsbn")
            inputISBNLayout.removeInputError()
            // showBookDataForIsbn()
        } else {
            Log.d(TAG, "Invalid ISBN set: $currentIsbn")
            inputISBNLayout.setInputError(R.string.error_invalid_isbn)
        }
    }

    private fun handleInvalidLengthIsbn() {
        inputISBNLayout.setInputError(R.string.error_input_isbn)
        //reset isIsbnSet
    }
}