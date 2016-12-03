package com.nibokapp.nibok.ui.fragment.publish

import android.support.design.widget.TextInputLayout
import android.view.View
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.toSafeInt
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_book_data.*
import java.util.*

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


    override fun getFragmentLayout(): Int = R.layout.fragment_publish_input_book_data

    override fun getInputContainer(): View = inputBookDataContainer

    override fun triggerInputsUpdate() {
        // Trigger in reverse order to highlight the first wrong form
        handlePublisherChange()
        handleYearChange()
        handleAuthorsChange()
        handleTitleChange()
    }

    override fun hasValidData(): Boolean {
        // Data is valid if no TextInputLayout has errors
        return inputLayouts.all { !it.hasError() }
    }

    override fun saveData() {
        val inputData = BookData("", titleText, getAuthorsList(), yearText.toSafeInt(), publisherText)
        publishProcessManager.setBookData(inputData)
    }

    override fun setupInput() {
        setupTextInput()
    }

    override fun onBecomeVisible() {
        super.onBecomeVisible()
        checkIfBookDataIsAvailable()
    }

    private fun checkIfBookDataIsAvailable() {
        val bookData = publishProcessManager.getInsertionData().bookData
        if (bookData.id != "") {
            displayBookData(bookData)
        } else {
            displayDefault(bookData)
        }
    }

    private fun displayBookData(bookData: BookData) {
        helperBookDetails.text = getString(R.string.review_book_details)
        with(bookData) {
            inputBookTitle.setText(title)
            inputBookAuthors.setText(authors.joinToString())
            inputBookYear.setText(year.toString().padStart(4, '0'))
            inputBookPublisher.setText(publisher)
        }
    }

    private fun displayDefault(bookData: BookData) {
        helperBookDetails.text = getString(R.string.add_book_details)

        // Prevent resetting already submitted data about a new book (no isbn set or book not found)
        val inputValues = listOf(titleText, authorsText, yearText, publisherText)
        val hasDataAboutUnknownBook = inputValues.any { it != "" } && bookData != BookData()
        if (hasDataAboutUnknownBook) return

        // Clear forms if the isbn was reset
        // and input forms are holding data about the discarded found (known) book
        inputBookTitle.setText("")
        inputBookAuthors.setText("")
        inputBookYear.setText("")
        inputBookPublisher.setText("")
        inputLayouts.reversed().forEach {
            it.error = null
            it.requestFocus()
        }
    }

    private fun getAuthorsList(): List<String> =
            authorsText.split(Regex("\\s*,\\s*")).map(String::trim).filter(String::isNotBlank)

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
            if (yearText.isBlank() || yearText.length < 4) {
                setInputError(R.string.error_required_year)
            } else if (yearText.toSafeInt() > Calendar.getInstance().get(Calendar.YEAR)) {
                setInputError(R.string.error_future_year)
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