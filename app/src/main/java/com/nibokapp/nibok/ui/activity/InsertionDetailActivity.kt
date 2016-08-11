package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.InsertionDetailFragment

class InsertionDetailActivity : AppCompatActivity() {

    companion object {
        private val TAG = InsertionDetailActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion_detail)

        // If restoring do not create overlapping fragments
        if (savedInstanceState != null) {
            return
        }


        val insertionDetailFragment = InsertionDetailFragment()
        // Pass eventual intent extras to the fragment
        insertionDetailFragment.arguments = intent.extras

        // Add the fragment to the container
        supportFragmentManager.beginTransaction()
                .add(R.id.detailFragmentContainer, insertionDetailFragment)
                .commit()

    }
}
