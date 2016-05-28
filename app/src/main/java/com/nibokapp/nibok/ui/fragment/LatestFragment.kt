package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.BookAdapter
import kotlinx.android.synthetic.main.latest_fragment.*

class LatestFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.latest_fragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        latestBooksList.setHasFixedSize(true)
        latestBooksList.layoutManager = LinearLayoutManager(context)

        initAdapter()

        // Mock books
        if (savedInstanceState == null) {
            val latestBooks = mutableListOf<BookModel>()
            for (i in 1..10) {
                latestBooks.add(
                        BookModel(
                                "Title is $i",
                                "Author num $i",
                                2000 + i,
                                "Light wear",
                                10 + i,
                                10 + i,
                                "http://lorempixel.com/300/400/abstract/$i"
                        )
                )
            }
            (latestBooksList.adapter as BookAdapter).addBooks(latestBooks)
        }

    }

    private fun initAdapter() {
        if (latestBooksList.adapter == null) {
            latestBooksList.adapter = BookAdapter()
        }
    }
}