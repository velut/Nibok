package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.extension.toSimpleDateString
import com.nibokapp.nibok.ui.presenter.InsertionDetailPresenter
import kotlinx.android.synthetic.main.content_insertion_detail.*
import kotlinx.android.synthetic.main.fragment_insertion_detail.*

class InsertionDetailFragment(val presenter: InsertionDetailPresenter = InsertionDetailPresenter()) :
        Fragment() {

    var actionBar: ActionBar? = null

    companion object {
        private val TAG = InsertionDetailFragment::class.java.simpleName
        val INSERTION_ID = "InsertionDetailFragment:insertionId"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_insertion_detail)
    }

    // TODO Listen to send message button

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
            val insertionId = it.getLong(INSERTION_ID)
            if (!insertionId.equals(0L)) { // Exclude default case of getLong()
                val data = presenter.getInsertionDetails(insertionId)
                data?.let { bindData(it) }
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
        insertionSoldBy.text = seller.name
        insertionDateField.text = insertionDate.toSimpleDateString()

        // Book details
        with(bookInfo) {

            // Set actionbar's title to book's title
            actionBar?.title = title

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
