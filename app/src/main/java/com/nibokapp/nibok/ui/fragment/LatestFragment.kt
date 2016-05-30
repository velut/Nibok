package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.BookManager
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.BookAdapter
import kotlinx.android.synthetic.main.latest_fragment.*

class LatestFragment : Fragment() {

    private val bookManager by lazy { BookManager() }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.latest_fragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        latestBooksList.setHasFixedSize(true)
        latestBooksList.layoutManager = LinearLayoutManager(context)

        initAdapter()

        val latestBooks = bookManager.getLatestBooks()
        (latestBooksList.adapter as BookAdapter).addBooks(latestBooks)

    }

    private fun initAdapter() {
        if (latestBooksList.adapter == null) {
            latestBooksList.adapter = BookAdapter()
        }
    }
}