package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
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
        setupPriceFilters()

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

    /**
     * Add input filters to the price input form to allow only well formed prices.
     */
    private fun setupPriceFilters() {

        val inputPrice = inputInsertionBookPrice

        val priceFilter = object : InputFilter {

            val MAX_INTEGER_DIGITS = 4
            val MAX_DECIMAL_DIGITS = 2
            val SEPARATOR = "."

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence? {

                val currentPrice = inputPrice.text.toString() + source.toString()
                val currentPriceLen = currentPrice.length
                val separatorIndex = currentPrice.indexOf(SEPARATOR)
                var result: String? = null

                when (separatorIndex) {
                // Price input is "." and gets replaced with "0."
                    0 -> result = "0$SEPARATOR"

                // Price contains only integer digits -> limit length if necessary
                    -1 -> if (currentPriceLen > MAX_INTEGER_DIGITS) result = ""

                // Price contains separator and may contain decimal digits
                //  -> limit length if necessary
                    else -> {
                        val decimalDigitsLen = currentPrice.substring(separatorIndex + 1).length
                        if (decimalDigitsLen > MAX_DECIMAL_DIGITS) result = ""
                    }
                }

                return result
            }
        }

        val zeroFilter = object : InputFilter {

            val SEPARATOR = "."

            override fun filter(source: CharSequence, start: Int, end: Int,
                                dest: Spanned, dstart: Int, dend: Int): CharSequence? {

                val oldPrice = inputPrice.text.toString()
                val nextChar = source.toString()
                var result: String? = null

                // A price starting with a 0 can only be in the form 0.xx, exclude prices where 0
                // is the leading digit and more digits follow before the separator (e.g. 0123.45)
                if (oldPrice == "0" && nextChar != SEPARATOR) {
                    result = ""
                }

                return result
            }
        }

        val filters = arrayOf(priceFilter, zeroFilter)
        inputInsertionBookPrice.filters = filters
    }
}
