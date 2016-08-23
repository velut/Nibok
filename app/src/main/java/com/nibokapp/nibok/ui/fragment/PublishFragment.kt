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

        /**
         * Values for ISBN parsing.
         */
        val ISBN_13_LENGTH = 13
        val ISBN_PREFIXES = listOf("977", "978", "979")
        val ISBN_PREFIX_LENGTH = 3

        /**
         * Keys for Bundle operations.
         */
        val KEY_CURRENT_PAGE = "$TAG:currentPage"
        val KEY_BOOK_DETAILS_HELPER_TEXT = "$TAG:bookDetailsHelperString"
        val KEY_IS_ISBN_SET = "$TAG:isISBNSet"

        /**
         * List of pages making up the insertion publishing process.
         */
        val PAGE_ISBN = 0
        val PAGE_BOOK_DETAILS = 1
        val PAGE_INSERTION_DETAILS = 2
    }

    /**
     * The current page being displayed.
     * By default the first page to display is the ISBN code input page.
     */
    private var currentPage = PAGE_ISBN

    /**
     * The mapping of pages to their views, initialized in onViewCreated.
     */
    lateinit var pages: Map<Int, View>

    /**
     * Helper text for the book's details page.
     */
    private var bookDetailsHelperText = ""

    /**
     * Signal if a valid ISBN code was set in the ISBN input view or not.
     */
    private var isISBNSet = false


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_publish)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURRENT_PAGE, currentPage)
        outState.putBoolean(KEY_IS_ISBN_SET, isISBNSet)
        outState.putString(KEY_BOOK_DETAILS_HELPER_TEXT, bookDetailsHelperText)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the pages map
        pages = mapOf(
                PAGE_ISBN to inputISBNContainer,
                PAGE_BOOK_DETAILS to inputBookDetailsContainer,
                PAGE_INSERTION_DETAILS to inputInsertionDetailsContainer
        )

        bookDetailsHelperText = getString(R.string.add_book_details)

        // Retrieve eventually saved values
        savedInstanceState?.let {
            currentPage = it.getInt(KEY_CURRENT_PAGE)

            bookDetailsHelperText = it.getString(KEY_BOOK_DETAILS_HELPER_TEXT,
                    getString(R.string.add_book_details))

            isISBNSet = it.getBoolean(KEY_IS_ISBN_SET)
        }

        helperBookDetails.text = bookDetailsHelperText

        showPage(currentPage)

        configureButtonNavigation()

        addInputISBNListener()

        setupBookConditionSpinner()
        setupPriceFilters()

    }

    /**
     * Add a listener to the ISBN code input to validate the code being entered.
     */
    private fun addInputISBNListener() {
        inputISBN.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        validateISBNInput()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                }
        )
    }

    /**
     * Initial validator for ISBN input.
     *
     * Check if the ISBN code length is valid or not.
     * If valid proceed to the next step otherwise show and error
     * and eventually reset old fetched book data.
     */
    private fun validateISBNInput() {
        val isbn = inputISBN.text.toString().trim()
        when (isbn.length) {
            ISBN_13_LENGTH -> parseISBN(isbn)
            else -> {
                inputISBNLayout.error = getString(R.string.error_input_isbn)
                inputISBNLayout.requestFocus()
                if (isISBNSet) {
                    isISBNSet = false
                    clearBookDetails()
                }
            }
        }
    }

    /**
     * Second validator for ISBN codes.
     *
     * If the code starts with a valid ISBN prefix then try to fetch the book's data and
     * show the next view, now the isbn is set.
     * Otherwise show that the code is invalid.
     */
    private fun parseISBN(isbn: String) {
        val isbnPrefix = isbn.substring(0, ISBN_PREFIX_LENGTH)
        when (isbnPrefix) {
            in ISBN_PREFIXES -> {
                inputISBNLayout.error = null
                showBookDataForISBN(isbn)
                isISBNSet = true
            }
            else -> {
                inputISBNLayout.error = getString(R.string.error_invalid_isbn)
                inputISBNLayout.requestFocus()
            }
        }
    }

    /**
     * Fetch book data for a given ISBN code, populate and show the book's details view
     * with the found data.
     */
    private fun showBookDataForISBN(isbn: String) {

        // If we set the isbn previously and the text listener was triggered
        // (e.g. after rotation) ignore the request
        if (isISBNSet) return

        Log.d(TAG, "Valid Isbn: $isbn")
        showPage(PAGE_BOOK_DETAILS)
        setBookHelperText(getString(R.string.review_book_details))

        // TODO get real data
        inputBookTitle.setText("Book Title Here")
        inputBookAuthors.setText("John Doe, Bob Zu")
        inputBookYear.setText("2016")
        inputBookPublisher.setText("Mit Press")
    }

    /**
     * Update the book helper text and keep track of the change.
     */
    private fun setBookHelperText(text: String) {
        bookDetailsHelperText = text
        helperBookDetails.text = text
    }

    /**
     * Configure the views' button navigation.
     */
    private fun configureButtonNavigation() {
        btnSkipISBN.setOnClickListener {
            showPage(PAGE_BOOK_DETAILS)
        }

        btnChangeISBN.setOnClickListener {
            showPage(PAGE_ISBN)
        }

        btnConfirmBookDetails.setOnClickListener {
            showPage(PAGE_INSERTION_DETAILS)
        }

        btnChangeBookDetails.setOnClickListener {
            showPage(PAGE_BOOK_DETAILS)
        }

        btnConfirmInsertionDetails.setOnClickListener {
            Log.d("SELL", "Everything confirmed")
        }
    }

    /**
     * Show the page at the given position and update the current page value,
     * hide all other pages before doing so.
     *
     *
     * @param pagePosition the position of the page to show
     */
    private fun showPage(pagePosition: Int) {
        pages.values.forEach { it.visibility = View.GONE }
        pages[pagePosition]?.visibility = View.VISIBLE
        currentPage = pagePosition
    }

    /**
     * Setup the spinner for the book's wear conditions.
     */
    private fun setupBookConditionSpinner() {
        val spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.book_condition_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputInsertionBookCondition.adapter = spinnerAdapter
    }

    /**
     * Reset the screen of the book's details.
     */
    private fun clearBookDetails() {
        setBookHelperText(getString(R.string.add_book_details))
        inputBookTitle.setText("")
        inputBookAuthors.setText("")
        inputBookYear.setText("")
        inputBookPublisher.setText("")
        inputBookDetailsContainer.scrollTo(0,0)
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
