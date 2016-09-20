package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.presenter.InsertionDetailPresenter
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.content_insertion_detail.*
import kotlinx.android.synthetic.main.fragment_insertion_detail.*


class InsertionDetailFragment(
        val presenter: InsertionDetailPresenter = InsertionDetailPresenter()
) : Fragment() {

    private var actionBar: ActionBar? = null

    companion object {
        private val TAG = InsertionDetailFragment::class.java.simpleName
        val INSERTION_ID = "InsertionDetailFragment:insertionId"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_insertion_detail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar = hostingActivity.supportActionBar
        actionBar?.title = getString(R.string.placeholder_book_detail)

        // Retrieve the insertion id and bind the data into the view
        arguments?.let {
            val insertionId = it.getString(INSERTION_ID)
            insertionId?.let {
                val data = presenter.getInsertionDetails(insertionId)
                data?.let {
                    setupFab(it)
                    setupImages(it)
                    bindData(it)
                }
            }
        }
    }

    private fun setupFab(data: BookInsertionModel) {

        try {
            if (data.seller.username == presenter.getUserId()) {
                return
            }
        } catch (e: IllegalStateException) {
            Log.d(TAG, "Skipping user id check")
        }

        fab.post { fab.visibility = View.VISIBLE }
        addFabListener(data)
    }

    private fun addFabListener(data: BookInsertionModel) {
        fab.setOnClickListener {
            val sellerId = data.seller.username

            if (presenter.loggedUserExists()) {
                val conversationId = presenter.startConversation(sellerId)
                conversationId?.let {
                    Log.d(TAG, "Starting conversation with user: $sellerId")
                    context.startConversation(conversationId)
                }
            } else {
                Log.d(TAG, "Guest needs to login before starting a conversation")
                context.startLoginActivity()
            }
        }
    }

    private fun setupImages(data: BookInsertionModel) {
        val pictures = data.bookPictureSources

        if (pictures.isNotEmpty()) {
            bookThumbnailImage.apply {
                // Load thumbnail in scrolling image view
                loadImg(pictures[0])

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
        insertionBookCondition.text = bookCondition
        insertionSoldBy.text = seller.username
        insertionDateField.text = insertionDate.toSimpleDateString()

        // Book details
        with(bookInfo) {

            // Set actionbar's title to book's title
            actionBar?.title = title

            // Bind book data

            detailBookTitle.text = title
            val numAuthors = authors.size
            val authorsString = authors.joinToString("\n")
            detailBookAuthorPlural.text =
                    resources.getQuantityString(R.plurals.book_author, numAuthors)
            detailBookAuthor.text = authorsString
            detailBookYear.text = year.toString()
            detailBookPublisher.text = publisher
            detailBookISBN.text = isbn
        }
    }
}
