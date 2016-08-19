package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.inflate
import kotlinx.android.synthetic.main.fragment_publish.*

class PublishFragment : Fragment() {

    companion object {
        private val TAG = PublishFragment::class.java.simpleName
        val ISBN_13_LENGTH = 13
        val ISBN_PREFIXES = listOf("977", "978", "979")
        val ISBN_PREFIX_LENGTH = 3
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_publish)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureButtonNavigation()

        inputISBN.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validateISBNInput()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }
        )

        setupBookConditionSpinner()

    }

    private fun validateISBNInput() {
        val isbn = inputISBN.text.toString().trim()
        when (isbn.length) {
            ISBN_13_LENGTH -> parseISBN(isbn)
            else -> {
                inputISBNLayout.error = getString(R.string.error_input_isbn)
                inputISBNLayout.requestFocus()}
        }
    }

    private fun parseISBN(isbn: String) {
        val isbnPrefix = isbn.substring(0, ISBN_PREFIX_LENGTH)
        when (isbnPrefix) {
            in ISBN_PREFIXES -> {
                inputISBNLayout.error = null
                getBookDataFromISBN(isbn)
            }
            else -> {
                inputISBNLayout.error = getString(R.string.error_invalid_isbn)
                inputISBNLayout.requestFocus()
            }
        }
    }

    private fun getBookDataFromISBN(isbn: String) {
        Log.d(TAG, "Valid Isbn: $isbn")
        inputISBNContainer.visibility = View.GONE
        inputBookDetailsContainer.visibility = View.VISIBLE
        helperBookDetails.text = getString(R.string.helper_book_details)

        // TODO get real data
        inputBookTitle.setText("Book Title Here")
        inputBookAuthors.setText("John Doe, Bob Zu")
        inputBookYear.setText("2016")
        inputBookPublisher.setText("Mit Press")
    }

    private fun configureButtonNavigation() {
        btnSkipISBN.setOnClickListener {
            inputISBNContainer.visibility = View.GONE
            helperBookDetails.text = getString(R.string.add_book_details)
            inputBookDetailsContainer.visibility = View.VISIBLE
        }

        btnChangeISBN.setOnClickListener {
            inputBookDetailsContainer.visibility = View.GONE
            inputISBNContainer.visibility = View.VISIBLE
        }

        btnConfirmBookDetails.setOnClickListener {
            inputBookDetailsContainer.visibility = View.GONE
            inputInsertionDetailsContainer.visibility = View.VISIBLE
        }

        btnChangeBookDetails.setOnClickListener {
            inputInsertionDetailsContainer.visibility = View.GONE
            inputBookDetailsContainer.visibility = View.VISIBLE
        }

        btnConfirmInsertionDetails.setOnClickListener {
            Log.d("SELL", "Everything confirmed")
        }
    }

    private fun setupBookConditionSpinner() {
        val spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.book_condition_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputInsertionBookCondition.adapter = spinnerAdapter
    }
}
