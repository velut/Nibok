package com.nibokapp.nibok.ui.fragment.publish

import android.support.design.widget.TextInputLayout
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.toSafeInt
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_book_data.*

/**
 * Publishing fragment managing input relative to data about the book,
 * such as title, authors, year, publisher.
 */
class InputBookData : BasePublishFragment() {

    companion object {
        private val TAG = InputBookData::class.java.simpleName
    }

    /**
     * Properties related to input data.
     */
    private val titleText: String
        get() = inputBookTitle.getStringText()

    private val authorsText: String
        get() = inputBookAuthors.getStringText()

    private val yearText: String
        get() = inputBookYear.getStringText()

    private val publisherText: String
        get() = inputBookPublisher.getStringText()

    /**
     * List of TextInputLayout associated to input data.
     */
    private val inputLayouts: List<TextInputLayout> by lazy {
        listOf(inputBookTitleLayout, inputBookAuthorsLayout,
                inputBookYearLayout, inputBookPublisherLayout)
    }


    override fun getFragmentLayout() : Int = R.layout.fragment_publish_input_book_data

    override fun getInputContainer() : View = inputBookDataContainer

    override fun triggerInputsUpdate() {
        handleTitleChange()
        handleAuthorsChange()
        handleYearChange()
        handlePublisherChange()
    }

    override fun hasValidData() : Boolean {
        // Data is valid if no TextInputLayout has errors
        return inputLayouts.all { !it.hasError() }
    }

    override fun saveData() {
        val inputData = BookData("", titleText, getAuthorsList(), yearText.toSafeInt(), publisherText)
        getPublishManager().setBookData(inputData)
    }

    override fun setupInput() {
        setupTextInput()
    }

    private fun getAuthorsList(): List<String> =
            authorsText.split(Regex("\\s*,\\s*")).map(String::trim)

    private fun setupTextInput() {
        inputBookTitle.afterTextChanged { handleTitleChange() }
        inputBookAuthors.afterTextChanged { handleAuthorsChange() }
        inputBookYear.afterTextChanged { handleYearChange() }
        inputBookPublisher.afterTextChanged { handlePublisherChange() }
    }

    private fun handleTitleChange() {
        with(inputBookTitleLayout) {
            if (titleText.isBlank()) {
                setInputError(R.string.error_required_title)
            } else {
                removeInputError()
            }
        }
    }

    private fun handleAuthorsChange() {
        with(inputBookAuthorsLayout) {
            if (authorsText.isBlank()) {
                setInputError(R.string.error_required_authors)
            } else {
                removeInputError()
            }
        }
    }

    private fun handleYearChange() {
        with(inputBookYearLayout) {
            if (yearText.isBlank()) {
                setInputError(R.string.error_required_year)
            } else {
                removeInputError()
            }
        }
    }

    private fun handlePublisherChange() {
        with(inputBookPublisherLayout) {
            if (publisherText.isBlank()) {
                setInputError(R.string.error_required_publisher)
            } else {
                removeInputError()
            }
        }
    }
}