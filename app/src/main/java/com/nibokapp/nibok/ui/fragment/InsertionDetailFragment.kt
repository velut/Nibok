package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.domain.model.DetailModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.extension.toSimpleDateString
import kotlinx.android.synthetic.main.content_insertion_detail.*
import kotlinx.android.synthetic.main.fragment_insertion_detail.*

class InsertionDetailFragment : Fragment() {

    companion object {
        private val TAG = InsertionDetailFragment::class.java.simpleName
        val INSERTION_ID = "InsertionDetailFragment:insertionId"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up the support action toolbar and the up button
        val hostingActivity = (activity as AppCompatActivity)
        hostingActivity.setSupportActionBar(toolbar)
        hostingActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Retrieve the insertion id and bind the data into the view
        arguments?.let {
            val insertionId = it.getLong(INSERTION_ID)
            if (!insertionId.equals(0L)) { // Exclude default case of getLong()
                val data = BookManager.getInsertionDetails(insertionId)
                data?.let { bindData(it) }
            }
        }
    }

    /**
     * Bind detail data into the view.
     *
     * @param item the item containing detail data
     */
    private fun bindData(item: DetailModel) {
        // Insertion details
        insertionBookPrice.text = item.bookPrice.toCurrency()
        insertionBookCondition.text = item.bookCondition
        insertionSoldBy.text = item.sellerName
        insertionDate.text = item.insertionDate.toSimpleDateString()

        // Book details
        detailBookTitle.text = item.bookTitle
        val numAuthors = item.bookAuthors.size
        val authors = item.bookAuthors.joinToString("\n")
        detailBookAuthorPlural.text = resources.getQuantityString(R.plurals.book_author, numAuthors)
        detailBookAuthor.text = authors
        detailBookYear.text = item.bookYear.toString()
        detailBookPublisher.text = item.bookPublisher
        detailBookISBN.text = item.bookISBN
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_insertion_detail)
    }
}
