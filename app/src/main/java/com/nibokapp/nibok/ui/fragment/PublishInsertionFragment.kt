package com.nibokapp.nibok.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.rule.IsbnValidator
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.filter.getPriceLeadingZerosFilter
import com.nibokapp.nibok.ui.filter.getPriceLengthFilter
import com.nibokapp.nibok.ui.presenter.PublishInsertionPresenter
import kotlinx.android.synthetic.main.fragment_publish_insertion.*
import kotlinx.android.synthetic.main.publish_input_book_details.*
import kotlinx.android.synthetic.main.publish_input_insertion_details.*
import kotlinx.android.synthetic.main.publish_input_isbn.*
import kotlinx.android.synthetic.main.publish_take_picture.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * PublishInsertionFragment hosts the views that constitute the insertion's publishing process.
 */
class PublishInsertionFragment(
        val presenter : PublishInsertionPresenter = PublishInsertionPresenter()
) : Fragment() {

    companion object {
        private val TAG = PublishInsertionFragment::class.java.simpleName

        /**
         * Keys for Bundle save and restore operations.
         */
        private val KEY_CURRENT_PAGE = "$TAG:currentPage"
        private val KEY_IS_ISBN_SET = "$TAG:isISBNSet"
        private val KEY_PICTURES_LIST = "$TAG:picturesUriList"
        private val KEY_CURRENT_PICTURE_URI = "$TAG:currentPictureUri"

        /**
         * Request code for picture taking.
         */
        const private val REQUEST_IMAGE_CAPTURE = 1

        /**
         * File provider authority constant.
         */
        const private val CAPTURE_PICTURES_FILE_PROVIDER = "com.nibokapp.nibok.fileprovider"

        /**
         * Constant for maximum number of pictures that the user can take.
         */
        const private val MAX_PICTURE_NUMBER = 5

        /**
         * Read and write permissions on a URI to be granted to the camera.
         */
        const private val URI_RW_PERMISSIONS =
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION

        /**
         * List of pages making up the insertion publishing process.
         */
        const private val PAGE_ISBN = 0
        const private val PAGE_BOOK_DETAILS = 1
        const private val PAGE_INSERTION_DETAILS = 2
        const private val PAGE_INSERTION_PICTURES = 3
    }

    /**
     * The mapping of pages to their views.
     */
    private val pages: Map<Int, View> by lazy {
        mapOf(
                PAGE_ISBN to inputISBNContainer,
                PAGE_BOOK_DETAILS to inputBookDetailsContainer,
                PAGE_INSERTION_DETAILS to inputInsertionDetailsContainer,
                PAGE_INSERTION_PICTURES to inputInsertionPicturesContainer
        )
    }

    /**
     * List of image views that hold the pictures taken by the user.
     */
    private val pictureImgHosts: List<ImageView> by lazy {
        listOf(picView1, picView2, picView3, picView4, picView5)
    }

    /**
     * The current page being displayed.
     * By default the first page to display is the ISBN code input page.
     */
    private var currentPage = PAGE_ISBN

    /**
     * List of URIs pointing to the images taken by the user.
     */
    private var picturesUriList = mutableListOf<String>()

    /**
     * Current Uri of the picture being taken.
     */
    private var currentPictureUri = ""

    /**
     * Dialogs notifying the user.
     */
    private var confirmationDialog: MaterialDialog? = null
    private var progressDialog: MaterialDialog? = null
    private var successDialog: MaterialDialog? = null
    private var errorDialog: MaterialDialog? = null

    /**
     * List of all dialogs.
     */
    private val dialogs: List<MaterialDialog?> = listOf(
            confirmationDialog, progressDialog, successDialog, errorDialog
    )

    /**
     * Signal if a valid ISBN code was set in the ISBN input view or not.
     */
    private var isISBNSet = false


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_publish_insertion)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.apply {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onPause() {
        super.onPause()
        // Dismiss dialogs to prevent leaked windows
        dialogs.forEach { it?.dismiss() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If we have a valid URI revoke previously granted permissions on it
        revokeUriPermissions()

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Picture taken, binding pictures")
                picturesUriList.add(currentPictureUri)
                bindPictures()
            } else {
                Log.d(TAG, "Image capture was unsuccessful, resetting URI")
                resetUri()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val picturesUri = picturesUriList.toCollection(ArrayList<String>())

        outState.apply {
            putInt(KEY_CURRENT_PAGE, currentPage)
            putBoolean(KEY_IS_ISBN_SET, isISBNSet)
            putString(KEY_CURRENT_PICTURE_URI, currentPictureUri)
            putStringArrayList(KEY_PICTURES_LIST, picturesUri)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve eventually saved values
        savedInstanceState?.let {
            currentPage = it.getInt(KEY_CURRENT_PAGE)

            isISBNSet = it.getBoolean(KEY_IS_ISBN_SET)

            currentPictureUri = it.getString(KEY_CURRENT_PICTURE_URI, "")

            picturesUriList = it.getStringArrayList(KEY_PICTURES_LIST).toMutableList()
        }

        // Bind the available pictures in the image views
        bindPictures()

        showPage(currentPage)

        addHideKeyboardListeners()
        configureButtonNavigation()

        addInputISBNListener()

        setupBookConditionSpinner()
        setupPriceFilters()
    }

    /**
     * Add touch listeners to all the page views.
     *
     * When the view is tapped hide the soft keyboard used for input.
     */
    private fun addHideKeyboardListeners() {
        val pageViews = pages.values
        pageViews.forEach {
            it.setOnTouchListener { view, motionEvent ->
                hideKeyboardListener(motionEvent, view, context)
            }
        }
    }

    /**
     * Display pictures taken by the user into the image views.
     */
    private fun bindPictures() {

        val picturesSize = picturesUriList.size

        if (picturesSize == 0) return

        Log.d(TAG, "Loading available pictures")
        loadPictures()

        if (picturesSize >= MAX_PICTURE_NUMBER) {
            Log.d(TAG, "Maximum number of pictures was taken. Disabling take picture button")
            disableTakePictureButton()
        } else {
            Log.d(TAG, "Scrolling to the last picture")
            scrollToLastPicture()
        }
    }

    private fun loadPictures() {
        // Take at most MAX_PICTURE_NUMBER to prevent loading into an unavailable ImageView
        val picturesToLoad = picturesUriList.take(MAX_PICTURE_NUMBER)
        picturesToLoad.forEachIndexed { index, pictureUri ->
            val host = pictureImgHosts[index]
            host.apply {
                visibility = View.VISIBLE
                loadImg(pictureUri)
            }
            Log.d(TAG, "ImgView: ${host.getName()} visible: ${host.visibility == View.VISIBLE}")
        }
    }

    private fun scrollToLastPicture() {
        pictureScrollView.postDelayed(
                { pictureScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) },
                120L)
    }

    private fun disableTakePictureButton() {
        btnTakePicture.apply {
            isEnabled = false
            visibility = View.GONE
        }
    }

    /**
     * Add a listener to the ISBN code input to validate the code being entered.
     */
    private fun addInputISBNListener() {
        inputISBN.afterTextChanged {
            validateISBNInput()
        }
    }

    /**
     * Validate codes submitted to the isbn input form.
     *
     * According to the validity of the current input display error messages and
     * set isISBNSet value.
     * If the isbn code is valid try to fetch corresponding book data.
     */
    private fun validateISBNInput() {
        val isbnCode = inputISBN.text.toString().trim()
        val validator = IsbnValidator()

        if (validator.isIsbnLengthValid(isbnCode)) {
            if (validator.isIsbnValid(isbnCode)) {
                Log.d(TAG, "Set valid ISBN")
                inputISBNLayout.error = null
                showBookDataForISBN(isbnCode)
                isISBNSet = true
                updateBookInputHelperText()
            } else {
                Log.d(TAG, "ISBN code is not valid")
                inputISBNLayout.error = getString(R.string.error_invalid_isbn)
                inputISBNLayout.requestFocus()
            }
        } else {
            Log.d(TAG, "ISBN code is too short")
            inputISBNLayout.error = getString(R.string.error_input_isbn)
            inputISBNLayout.requestFocus()
            if (isISBNSet) {
                isISBNSet = false
                clearBookDetails()
                updateBookInputHelperText()
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

        // TODO get real data
//        presenter.getBookDataByISBN(isbn)
        inputBookTitle.setText("Book Title Here")
        inputBookAuthors.setText("John Doe, Bob Zu")
        inputBookYear.setText("2016")
        inputBookPublisher.setText("Mit Press")
    }

    /**
     * Configure the views' button navigation.
     */
    private fun configureButtonNavigation() {

        btnSkipISBN.onClickShowPage(PAGE_BOOK_DETAILS)
        btnChangeISBN.onClickShowPage(PAGE_ISBN)
        btnConfirmBookDetails.onClickShowPage(PAGE_INSERTION_DETAILS)
        btnChangeBookDetails.onClickShowPage(PAGE_BOOK_DETAILS)
        btnConfirmInsertionDetails.onClickShowPage(PAGE_INSERTION_PICTURES)
        btnChangeInsertionDetails.onClickShowPage(PAGE_INSERTION_DETAILS)

        btnTakePicture.setOnClickListener {
            dispatchTakePictureIntent()
        }

        btnFinalizeInsertion.setOnClickListener {
            showPublishAlertDialog()
        }
    }

    /**
     * Show alert dialog with insertion info recap before publishing
     * and ask user if he really wants to publish the insertion.
     * Show a progress dialog while the insertion is being published.
     */
    private fun showPublishAlertDialog() {
        progressDialog = MaterialDialog.Builder(context)
                .content(getString(R.string.progress_publishing))
                .progress(true, 0) // Indeterminate progress dialog
                .cancelable(false) // Clicking outside the dialog does not close it
                .build()

        successDialog = MaterialDialog.Builder(context)
                .title(getString(R.string.publish_insertion_success_title))
                .content(getString(R.string.publish_insertion_success_content))
                .positiveText(getString(R.string.publish_insertion_success_quit))
                .onPositive { materialDialog, dialogAction -> activity.finish() }
                .build()

        errorDialog = MaterialDialog.Builder(context)
                .title(getString(R.string.publish_insertion_error_title))
                .content(getString(R.string.publish_insertion_error_content))
                .positiveText(getString(android.R.string.ok))
                .build()

        val confirmationMessage = getPublishAlertDialogContent()

        confirmationDialog = MaterialDialog.Builder(context)
                .title(getString(R.string.alert_publish_title))
                .content(confirmationMessage)
                .positiveText(getString(R.string.alert_publish_positive_text))
                .negativeText(getString(R.string.text_cancel))
                .onPositive { materialDialog, dialogAction ->
                    progressDialog?.show()
                    // TODO publish insertion
                    doAsync {
                        val published = presenter.publishInsertion()
                        uiThread {
                            progressDialog?.dismiss()
                            if (published) {
                                successDialog?.show()
                            } else {
                                errorDialog?.show()
                            }
                        }
                    }
                }
                .build()

        confirmationDialog?.show()
    }

    /**
     * Build the content string used in the publish alert dialog
     * representing an overview of the insertion data submitted by the user.
     *
     * @return a string summarising the data that the user submitted for the insertion
     */
    private fun getPublishAlertDialogContent(): String {
        val confirmationQuestion = getString(R.string.alert_publish_content_question)
        val title = getString(R.string.book_title)
        val authors = resources.getQuantityString(R.plurals.book_author, 2)
        val year = getString(R.string.book_year)
        val publisher = getString(R.string.book_publisher)
        val isbn = getString(R.string.book_isbn)
        val price = getString(R.string.insertion_book_price)
        val wearCondition = getString(R.string.book_wear_condition)

        // TODO Insert book data
        val dialogContent = """
        |$confirmationQuestion
        |
        |$title:
        |$authors:
        |$year:
        |$publisher:
        |$isbn:
        |$price:
        |$wearCondition:
        """.trimMargin()

        return dialogContent
    }

    /**
     * Dispatch the intent to take a picture to already installed apps.
     */
    private fun dispatchTakePictureIntent() {

        val pictureFile = createImageFile(context)

        if (pictureFile == null) {
            Log.d(TAG, "Could not create a picture file")
            // TODO dialog?
            return
        }

        val pictureURI = FileProvider.getUriForFile(context,
                CAPTURE_PICTURES_FILE_PROVIDER, pictureFile)

        currentPictureUri = pictureURI.toString()

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
            Log.d(TAG, "Granting URI permission to: $packageName")
            context.grantUriPermission(packageName, pictureURI, URI_RW_PERMISSIONS)
        }

        // Finally start the activity to take the picture
        val takePictureActivity = takePictureIntent.resolveActivity(activity.packageManager)
        takePictureActivity?.let {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * Show the page at the given position and update the current page value,
     * hide all other pages before doing so.
     * Hide also the soft keyboard when the page is shown.
     *
     * @param pagePosition the position of the page to show
     */
    private fun showPage(pagePosition: Int) {
        pages.values.forEach { it.visibility = View.GONE }
        currentPage = pagePosition
        val currentView = pages[pagePosition]
        currentView?.apply {
            visibility = View.VISIBLE
            hideSoftKeyboard(context)
        }
        if (currentPage == PAGE_BOOK_DETAILS) updateBookInputHelperText()
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
     * Reset the screen of the book's details and scroll to top.
     */
    private fun clearBookDetails() {
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
        inputInsertionBookPrice.filters =
                arrayOf(getPriceLengthFilter(inputPrice), getPriceLeadingZerosFilter(inputPrice))
    }

    /**
     * Revoke previously granted write permissions on the current URI.
     */
    private fun revokeUriPermissions() {
        if (currentPictureUri != "") {
            context.revokeUriPermission(Uri.parse(currentPictureUri), URI_RW_PERMISSIONS)
        }
    }

    /**
     * Delete the unused file associated to the last URI.
     * Reset the current URI.
     */
    private fun resetUri() {
        context.contentResolver.delete(Uri.parse(currentPictureUri), null, null)
        currentPictureUri = ""
    }

    /**
     * Update the helper text for book details input,
     * suggesting the user either to insert or review book details.
     */
    private fun updateBookInputHelperText() {
        helperBookDetails.text =
                if (isISBNSet) getString(R.string.review_book_details)
                else getString(R.string.add_book_details)
    }

    private fun Button.onClickShowPage(page: Int) {
        this.setOnClickListener {
            showPage(page)
        }
    }
}
