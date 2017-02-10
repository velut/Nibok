package com.nibokapp.nibok.ui.fragment.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.presenter.detail.InsertionDetailPresenter
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.content_insertion_detail.*
import kotlinx.android.synthetic.main.fragment_insertion_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * InsertionDetailFragment hosts the insertion's detail view.
 *
 * From this view the user can:
 *  - see details about the insertion
 *  - open a gallery to see eventual pictures associated to the insertion
 *  - contact the seller
 */
class InsertionDetailFragment(
        val presenter: InsertionDetailPresenter = InsertionDetailPresenter()
) : Fragment() {

    companion object {
        private val TAG = InsertionDetailFragment::class.java.simpleName

        /**
         * Key for arguments passing.
         */
        val INSERTION_ID = "$TAG:insertionId"
    }

    /**
     * Insertion's id.
     */
    private var insertionId: String? = null

    /**
     * Seller's id.
     * Only set if insertion data is valid.
     */
    private lateinit var sellerId: String


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_insertion_detail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.apply {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.placeholder_book_detail)
        }

        // Retrieve insertionId
        insertionId = arguments?.getString(INSERTION_ID)

        setupView()
    }

    private fun setupView() {

        // If insertionId was not set return
        val id = insertionId ?: return

        doAsync {
            val data = presenter.getInsertionDetails(id)
            uiThread {
                data?.let {
                    setSellerId(it)
                    bindData(it)
                    setupImages(it)
                    setupFab()
                }
            }
        }
    }

    private fun setSellerId(data: BookInsertionModel) {
        sellerId = data.seller.username
    }

    private fun setupFab() {
        val userId = presenter.getUserId()
        if (userId == null || userId != sellerId) {
            Log.d(TAG, "Enabling fab")
            enableFab()
        } else {
            Log.d(TAG, "Fab was not enabled")
        }
    }

    private fun enableFab() {
        fab.apply {
            setOnClickListener {
                val localUserExists = presenter.loggedUserExists()
                if (localUserExists) {
                    startConversation()
                } else {
                    showAuthActivity()
                }
            }
            post { fab.show() }
        }
    }

    private fun startConversation() {
        doAsync {
            val conversationId = presenter.startConversation(sellerId)
            uiThread {
                if (conversationId != null) {
                    Log.d(TAG, "Starting conversation with user: $sellerId")
                    context.startChatActivity(conversationId)
                } else {
                    Log.d(TAG, "Could not start a conversation with: $sellerId")
                    context.toast(R.string.error_conversation_not_started)
                }
            }
        }
    }

    private fun showAuthActivity() {
        Log.d(TAG, "Guest needs to login before starting a conversation")
        context.startAuthenticateActivity()
    }

    private fun setupImages(data: BookInsertionModel) {
        val pictures = data.bookPictureSources

        if (pictures.isNotEmpty()) {
            bookThumbnailImage.apply {
                // Load thumbnail in scrolling image view
                loadImage(pictures[0])

                // Set click listener for gallery
                setOnClickListener {
                    Log.d(TAG, "Opening book pictures gallery")
                    ImageViewer.Builder(context, pictures.toTypedArray()).show()
                }
            }
        }
    }

    /**
     * Bind detail data into the view.
     *
     * @param item the item containing detail data
     */
    private fun bindData(item: BookInsertionModel) = with(item) {
        // Insertion details
        insertionBookPrice.text = bookPrice.toCurrency()
        bookCondition.toBookWearCondition(context)?.let {
            insertionBookCondition.text = it
        }
        insertionSoldBy.text = seller.username
        insertionDateField.text = insertionDate.toSimpleDateString()

        // Book details
        with(bookInfo) {

            // Update activity's title using book's title
            // Scrolling activity must use toolbar_layout to update title
            toolbar_layout?.title = title

            // Bind book data
            detailBookTitle.text = title
            val numAuthors = authors.size
            val authorsString = authors.joinToString("\n")
            detailBookAuthorPlural.text =
                    resources.getQuantityString(R.plurals.book_author, numAuthors)
            detailBookAuthor.text = authorsString
            detailBookYear.text = year.toString()
            detailBookPublisher.text = publisher
            if (isbn != "") {
                detailBookISBN.text = isbn
            }
        }
    }
}
