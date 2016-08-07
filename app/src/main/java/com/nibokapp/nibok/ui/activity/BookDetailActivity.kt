package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.BookDetailFragment

class BookDetailActivity : AppCompatActivity() {

    companion object {
        private val TAG = BookDetailActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // If restoring do not create overlapping fragments
        if (savedInstanceState != null) {
            return
        }


        val bookDetailFragment = BookDetailFragment()
        // Pass eventual intent extras to the fragment
        bookDetailFragment.arguments = intent.extras

        // Add the fragment to the container
        supportFragmentManager.beginTransaction()
                .add(R.id.detailFragmentContainer, bookDetailFragment)
                .commit()

    }
}
