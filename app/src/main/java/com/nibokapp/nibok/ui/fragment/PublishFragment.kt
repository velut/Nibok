package com.nibokapp.nibok.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
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
import android.widget.HorizontalScrollView
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import kotlinx.android.synthetic.main.fragment_publish.*
import kotlinx.android.synthetic.main.publish_input_book_details.*
import kotlinx.android.synthetic.main.publish_input_insertion_details.*
import kotlinx.android.synthetic.main.publish_input_isbn.*
import kotlinx.android.synthetic.main.publish_take_picture.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
         * Keys for Bundle save and restore operations.
         */
        val KEY_CURRENT_PAGE = "$TAG:currentPage"
        val KEY_BOOK_DETAILS_HELPER_TEXT = "$TAG:bookDetailsHelperString"
        val KEY_IS_ISBN_SET = "$TAG:isISBNSet"
        val KEY_PICTURES_LIST = "$TAG:picturesUriList"

        /**
         * Request code for picture taking
         */
        val REQUEST_IMAGE_CAPTURE = 1

        /**
         * File provider authority constant
         */
        val CAPTURE_PICTURES_FILE_PROVIDER = "com.nibokapp.nibok.fileprovider"

        /**
         * Constant for maximum number of pictures that the user can take
         */
        val MAX_PICTURE_NUMBER = 5

        /**
         * List of pages making up the insertion publishing process.
         */
        val PAGE_ISBN = 0
        val PAGE_BOOK_DETAILS = 1
        val PAGE_INSERTION_DETAILS = 2
        val PAGE_INSERTION_PICTURES = 3
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

    private var picturesUriList = mutableListOf<String>()

    /**
     * List of image views that hold the pictures taken by the user.
     */
    lateinit var pictureImgHosts: List<ImageView>


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                // TODO Check update
                bindPictures()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_CURRENT_PAGE, currentPage)
        outState.putBoolean(KEY_IS_ISBN_SET, isISBNSet)
        outState.putString(KEY_BOOK_DETAILS_HELPER_TEXT, bookDetailsHelperText)
        outState.putStringArrayList(KEY_PICTURES_LIST, picturesUriList.toCollection(ArrayList<String>()))
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the pages map
        pages = mapOf(
                PAGE_ISBN to inputISBNContainer,
                PAGE_BOOK_DETAILS to inputBookDetailsContainer,
                PAGE_INSERTION_DETAILS to inputInsertionDetailsContainer,
                PAGE_INSERTION_PICTURES to inputInsertionPicturesContainer
        )

        // Initialize picture hosting views
        pictureImgHosts = listOf(picView1, picView2, picView3, picView4, picView5)

        bookDetailsHelperText = getString(R.string.add_book_details)

        // Retrieve eventually saved values
        savedInstanceState?.let {
            currentPage = it.getInt(KEY_CURRENT_PAGE)

            bookDetailsHelperText = it.getString(KEY_BOOK_DETAILS_HELPER_TEXT,
                    getString(R.string.add_book_details))

            isISBNSet = it.getBoolean(KEY_IS_ISBN_SET)

            picturesUriList = it.getStringArrayList(KEY_PICTURES_LIST).toMutableList()
        }

        helperBookDetails.text = bookDetailsHelperText

        // TODO close keyboards
        // TODO check for horizontal scroll in landscape

        // Bind the available pictures in the image views
        bindPictures()

        showPage(currentPage)

        configureButtonNavigation()

        addInputISBNListener()

        setupBookConditionSpinner()
        setupPriceFilters()

    }

    private fun bindPictures() {
        if (picturesUriList.isNotEmpty()) {
            if (picturesUriList.size == MAX_PICTURE_NUMBER) {
                btnTakePicture.visibility = View.GONE
                //picEndSpacing.visibility = View.GONE
            } else {
                //picEndSpacing.visibility = View.VISIBLE
            }
            picturesUriList.forEachIndexed { index, pictureUri ->
                val host = pictureImgHosts[index]
                host.loadImg(pictureUri)
                host.visibility = View.VISIBLE
            }
            pictureScrollView.postDelayed(
                    {pictureScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)},
                    120L)
        }
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
            showPage(PAGE_INSERTION_PICTURES)
        }

        btnChangeInsertionDetails.setOnClickListener {
            showPage(PAGE_INSERTION_DETAILS)
        }

        btnTakePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    /**
     * Dispatch the intent to take a picture to already installed apps.
     */
    private fun dispatchTakePictureIntent() {

        val pictureFile = createImageFile()

        pictureFile?.let {
            val pictureURI = FileProvider.getUriForFile(context,
                    CAPTURE_PICTURES_FILE_PROVIDER,
                    it)

            // Add the picture uri to the current pictures list
            picturesUriList.add(pictureURI.toString())

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Default activities that can take a picture
            val resolvedIntentActivities = context.packageManager
                    .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY)

            // Grant to each of these activities the permission to read
            // and write to the picture URI.
            // If this is not done SecurityException is raised and
            // the camera apps crash.
            resolvedIntentActivities.forEach {
                val packageName = it.activityInfo.packageName
                Log.d(TAG, "Granting permission to: $packageName")
                context.grantUriPermission(packageName, pictureURI,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Finally start the activity to take the picture
            val takePictureActivity = takePictureIntent.resolveActivity(activity.packageManager)
            takePictureActivity?.let {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }

            // TODO Revoke permissions at the right time
            //context.revokeUriPermission(pictureURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        }

    /**
     * Try to create an unique image file in which to store a picture.
     *
     * @return a File if the file was created successfully,
     * null if no file could be created
     */
    private fun createImageFile() : File? {
        var imageFile: File? = null

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timestamp"
        val extension = ".jpg"

        // Get the external public files directory
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            imageFile = File.createTempFile(imageFileName, extension, storageDir)
        } catch (ex: IOException) {
            Log.d(TAG, "Could not create image file\nException:$ex")
        }

        return imageFile
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
