package com.nibokapp.nibok.ui.fragment.publish

import android.util.Log
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.IsbnValidator
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_isbn.*

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

    override fun setupInput() {
        inputISBN.afterTextChanged {
            validateInputIsbn()
        }
    }

    private fun validateInputIsbn() {
        if (isbnValidator.isIsbnLengthValid(currentIsbn)) {
            handleValidLengthIsbn()
        } else {
            handleInvalidLengthIsbn()
        }
    }

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