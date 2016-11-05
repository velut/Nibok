package com.nibokapp.nibok.ui.fragment.publish

import android.support.design.widget.TextInputLayout
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_book_data.*

class InputBookData : BasePublishFragment() {

    companion object {
        private val TAG = InputBookData::class.java.simpleName
    }

    private val titleText: String
        get() = inputBookTitle.getStringText()

    private val authorsText: String
        get() = inputBookAuthors.getStringText()

    private val yearText: String
        get() = inputBookYear.getStringText()

    private val publisherText: String
        get() = inputBookPublisher.getStringText()

    private val inputLayouts: List<TextInputLayout> by lazy {
        listOf(inputBookTitleLayout, inputBookAuthorsLayout,
                inputBookYearLayout, inputBookPublisherLayout)
    }


    override fun getFragmentLayout() : Int = R.layout.fragment_publish_input_book_data

    override fun getInputContainer() : View = inputBookDataContainer

    override fun hasValidData() : Boolean {
        return inputLayouts.all { !it.hasError() }
    }

    override fun setupInput() {
        inputBookTitle.afterTextChanged { handleTitleChange() }
        inputBookAuthors.afterTextChanged { handleAuthorsChange() }
        inputBookYear.afterTextChanged { handleYearChange() }
        inputBookPublisher.afterTextChanged { handlePublisherChange() }
    }

    private fun handleTitleChange() {
        with(inputBookTitleLayout) {
            if (titleText.isBlank()) {
                setInputError("Title is required")
            } else {
                removeInputError()
            }
        }
    }

    private fun handleAuthorsChange() {
        with(inputBookAuthorsLayout) {
            if (authorsText.isBlank()) {
                setInputError("Authors are required")
            } else {
                removeInputError()
            }
        }
    }

    private fun handleYearChange() {
        with(inputBookYearLayout) {
            if (yearText.isBlank()) {
                setInputError("Year is required")
            } else {
                removeInputError()
            }
        }
    }

    private fun handlePublisherChange() {
        with(inputBookPublisherLayout) {
            if (publisherText.isBlank()) {
                setInputError("Publisher is required")
            } else {
                removeInputError()
            }
        }
    }
}